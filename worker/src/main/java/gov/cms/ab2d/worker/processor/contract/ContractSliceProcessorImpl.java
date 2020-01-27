package gov.cms.ab2d.worker.processor.contract;

import gov.cms.ab2d.common.model.JobOutput;
import gov.cms.ab2d.common.model.JobProgress;
import gov.cms.ab2d.common.model.OptOut;
import gov.cms.ab2d.common.repository.JobOutputRepository;
import gov.cms.ab2d.common.repository.JobProgressRepository;
import gov.cms.ab2d.common.repository.JobRepository;
import gov.cms.ab2d.common.repository.OptOutRepository;
import gov.cms.ab2d.worker.adapter.bluebutton.GetPatientsByContractResponse.PatientDTO;
import gov.cms.ab2d.worker.processor.PatientClaimsProcessor;
import gov.cms.ab2d.worker.processor.domainmodel.ContractData;
import gov.cms.ab2d.worker.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import static gov.cms.ab2d.common.model.JobStatus.IN_PROGRESS;
import static gov.cms.ab2d.common.util.Constants.EOB;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractSliceProcessorImpl implements ContractSliceProcessor {
    private static final String DATA_FILE_SUFFIX = ".ndjson";
    private static final String ERROR_FILE_SUFFIX = "_error.ndjson";

    @Value("${cancellation.check.frequency:10}")
    private int cancellationCheckFrequency;

    @Value("${report.progress.db.frequency:100}")
    private int reportProgressDbFrequency;

    @Value("${report.progress.log.frequency:100}")
    private int reportProgressLogFrequency;

    private final FileService fileService;
    private final JobRepository jobRepository;
    private final JobProgressRepository jobProgressRepository;
    private final JobOutputRepository jobOutputRepository;
    private final OptOutRepository optOutRepository;
    private final PatientClaimsProcessor patientClaimsProcessor;

    @Override
    @Async("patientProcessorThreadPool")
    public CompletableFuture<Void> process(Map.Entry<Integer, List<PatientDTO>> slice, ContractData contractData) {
        if (!isJobStillInProgress(contractData)) {
            return CompletableFuture.completedFuture(null);
        }

        var startedAt = Instant.now();
        var sliceSno = slice.getKey();
        log.info("Slice [{}] has [{}] patients ", sliceSno, slice.getValue().size());

        // create output data and error file
        Path dataFile = getOutputFile(contractData, sliceSno, DATA_FILE_SUFFIX);
        Path errorFile = getOutputFile(contractData, sliceSno, ERROR_FILE_SUFFIX);

        var jobUuid = contractData.getJobDM().getJobUuid();
        var patientsInSlice = slice.getValue();
        var patientCountInSlice = patientsInSlice.size();
        var jobProgress = findJobProgress(contractData, sliceSno);
        try {
            int errorCount = processPatients(contractData, dataFile, errorFile, patientsInSlice, jobProgress);
            createJobOutputs(dataFile, errorFile, jobUuid, patientCountInSlice, errorCount);
        } catch (RuntimeException e) {
            var errMsg = "Update job [%s] to FAILED status";
            log.error(String.format(errMsg, jobUuid), e);

            var failureMessage = ExceptionUtils.getRootCauseMessage(e);
            jobRepository.saveJobFailure(jobUuid, failureMessage);
        }

        logTimeTaken(slice.getKey(), startedAt);
        return CompletableFuture.completedFuture(null);
    }


    private boolean isJobStillInProgress(ContractData contractData) {
        var jobUuid = contractData.getJobDM().getJobUuid();
        var jobStatus = jobRepository.findJobStatus(jobUuid);
        if (IN_PROGRESS.equals(jobStatus)) {
            return true;
        }

        var errMsg = "Job [%s] is no longer in progress. It is in [%s] status. Stopping processing";
        log.warn("{}", String.format(errMsg, jobUuid, jobStatus));
        return false;
    }


    private Path getOutputFile(ContractData contractData, Integer sliceSno, String suffix) {
        var outputDir = contractData.getOutputDir();
        var contractNumber = contractData.getContract().getContractNumber();
        var dataFilename = createFileName(contractNumber, sliceSno, suffix);
        return fileService.createOrReplaceFile(outputDir, dataFilename);
    }


    private JobProgress findJobProgress(ContractData contractData, Integer sliceSno) {
        var jobId = contractData.getJobDM().getJobId();
        var contractId = contractData.getContract().getId();
        return jobProgressRepository.findOne(jobId, contractId, sliceSno);
    }


    private int processPatients(ContractData contractData, Path dataFile, Path errorFile, List<PatientDTO> patientsSlice, JobProgress jobProgress) {
        int totalCountInSlice = patientsSlice.size();
        int errorCount = 0;
        int recordsProcessedCount = 0;
        int lastPercentCompleted = 0;
        for (PatientDTO patient : patientsSlice) {
            ++recordsProcessedCount;

            if (recordsProcessedCount % cancellationCheckFrequency == 0) {
                if (!isJobStillInProgress(contractData)) {
                    break;
                }
            }

            if (recordsProcessedCount % reportProgressDbFrequency == 0) {
                lastPercentCompleted = updateProgressInDb(jobProgress, totalCountInSlice, recordsProcessedCount, lastPercentCompleted);
            }

            if (recordsProcessedCount % reportProgressLogFrequency == 0) {
                final int percentCompleted = (recordsProcessedCount * 100) / totalCountInSlice;
                log.info("[{}/{}] records processed = [{}% completed]", recordsProcessedCount, totalCountInSlice, percentCompleted);
            }

            var patientId = patient.getPatientId();
            if (isOptOutPatient(patientId)) {
                continue;       // this patient has opted out. skip patient record.
            }

            errorCount += patientClaimsProcessor.process(patientId, new ReentrantLock(), dataFile, errorFile);
        }

        updateProgressInDb(jobProgress, totalCountInSlice, recordsProcessedCount, lastPercentCompleted);
        return errorCount;
    }


    private int updateProgressInDb(JobProgress jobProgress, int totalCountInSlice, int recordsProcessedCount, int lastPercentCompleted) {
        final int percentCompleted = (recordsProcessedCount * 100) / totalCountInSlice;
        if (percentCompleted > lastPercentCompleted) {
            jobProgress.setRecordsProcessed(recordsProcessedCount);
            jobProgressRepository.saveAndFlush(jobProgress);
        }
        return percentCompleted;
    }


    private boolean isOptOutPatient(String patientId) {

        final List<OptOut> optOuts = optOutRepository.findByHicn(patientId);
        if (optOuts.isEmpty()) {
            // No opt-out record found for this patient - Opt-In by default.
            return false;
        }

        // opt-out record has an effective date.
        // if any of the opt-out records for a patient is effective as of today or earlier, the patient has opted-out
        final LocalDate tomorrow = LocalDate.now().plusDays(1);
        return optOuts.stream()
                .anyMatch(optOut -> optOut.getEffectiveDate().isBefore(tomorrow));
    }


    private String createFileName(String contractNumber, Integer key, String outputFileSuffix) {
        return new StringBuilder()
                .append(contractNumber)
                .append("_")
                .append(StringUtils.leftPad(key.toString(), 5, '0'))
                .append(outputFileSuffix)
                .toString();
    }


    private void createJobOutputs(Path dataFile, Path errorFile, String jobUuid, int patientCountInSlice, int errorCount) {
        var jobOutputs = new ArrayList<JobOutput>();
        if (errorCount < patientCountInSlice) {
            jobOutputs.add(createJobOutput(dataFile, false));
        }
        if (errorCount > 0) {
            log.warn("Encountered {} errors during job processing", errorCount);
            jobOutputs.add(createJobOutput(errorFile, true));
        }

        var job = jobRepository.findByJobUuid(jobUuid);
        jobOutputs.forEach(jobOutput -> job.addJobOutput(jobOutput));
        jobOutputRepository.saveAll(jobOutputs);
    }


    private JobOutput createJobOutput(Path outputFile, boolean isError) {
        JobOutput jobOutput = new JobOutput();
        jobOutput.setFilePath(outputFile.getFileName().toString());
        jobOutput.setFhirResourceType(EOB);
        jobOutput.setError(isError);
        return jobOutput;
    }


    private void logTimeTaken(int sliceSno, Instant start) {
        var timeTaken = Duration.between(start, Instant.now()).toSeconds();
        log.info("Slice [{}] completed in [{}] seconds", sliceSno, timeTaken);
    }


}