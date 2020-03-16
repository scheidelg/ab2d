package gov.cms.ab2d.worker.processor;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import gov.cms.ab2d.common.model.Contract;
import gov.cms.ab2d.common.model.Job;
import gov.cms.ab2d.common.model.JobOutput;
import gov.cms.ab2d.common.model.JobStatus;
import gov.cms.ab2d.common.model.OptOut;
import gov.cms.ab2d.common.model.Sponsor;
import gov.cms.ab2d.common.repository.JobOutputRepository;
import gov.cms.ab2d.common.repository.JobRepository;
import gov.cms.ab2d.common.repository.OptOutRepository;
import gov.cms.ab2d.common.util.Constants;
import gov.cms.ab2d.worker.adapter.bluebutton.ContractAdapter;
import gov.cms.ab2d.worker.adapter.bluebutton.GetPatientsByContractResponse;
import gov.cms.ab2d.worker.adapter.bluebutton.GetPatientsByContractResponse.PatientDTO;
import gov.cms.ab2d.worker.config.RoundRobinBlockingQueue;
import gov.cms.ab2d.worker.processor.StreamHelperImpl.FileOutputType;
import gov.cms.ab2d.worker.processor.domainmodel.ContractData;
import gov.cms.ab2d.worker.processor.domainmodel.ProgressTracker;
import gov.cms.ab2d.worker.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static gov.cms.ab2d.common.model.JobStatus.CANCELLED;
import static gov.cms.ab2d.common.model.JobStatus.SUCCESSFUL;
import static gov.cms.ab2d.common.service.JobServiceImpl.ZIPFORMAT;
import static gov.cms.ab2d.common.util.Constants.CONTRACT_LOG;
import static gov.cms.ab2d.common.util.Constants.EOB;
import static gov.cms.ab2d.worker.processor.StreamHelperImpl.FileOutputType.NDJSON;
import static gov.cms.ab2d.worker.processor.StreamHelperImpl.FileOutputType.ZIP;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyStaticImports")
public class JobProcessorImpl implements JobProcessor {
    private static final int SLEEP_DURATION = 250;

    @Value("${job.file.rollover.ndjson:200}")
    private long ndjsonRollOver;

    @Value("${job.file.rollover.zip:200}")
    private long zipRollOver;

    @Value("${cancellation.check.frequency:10}")
    private int cancellationCheckFrequency;

    @Value("${report.progress.db.frequency:100}")
    private int reportProgressDbFrequency;

    @Value("${report.progress.log.frequency:100}")
    private int reportProgressLogFrequency;

    @Value("${file.try.lock.timeout}")
    private int tryLockTimeout;

    /** Failure threshold an integer expressed as a percentage of failure tolerated in a batch **/
    @Value("${failure.threshold}")
    private int failureThreshold;

    @Value("${efs.mount}")
    private String efsMount;

    @Value("${audit.files.ttl.hours}")
    private int auditFilesTTLHours;

    private final FileService fileService;
    private final JobRepository jobRepository;
    private final JobOutputRepository jobOutputRepository;
    private final ContractAdapter contractAdapter;
    private final PatientClaimsProcessor patientClaimsProcessor;
    private final OptOutRepository optOutRepository;

    /**
     * Load the job and process it
     *
     * @param jobUuid - the job id
     * @return the processed job
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    @Trace(metricName = "Job Processing", dispatcher = true)
    public Job process(final String jobUuid) {

        // Load the job
        final Job job = jobRepository.findByJobUuid(jobUuid);
        log.info("Found job");

        // Determine the output directory based on the job id
        Path outputDirPath = null;
        try {
            outputDirPath = Paths.get(efsMount, jobUuid);
            processJob(job, outputDirPath);

        } catch (JobCancelledException e) {
            log.warn("Job: [{}] CANCELLED", jobUuid);

            log.info("Deleting output directory : {} ", outputDirPath.toAbsolutePath());
            deleteExistingDirectory(outputDirPath);

        } catch (Exception e) {
            log.error("Unexpected expection ", e);
            job.setStatus(JobStatus.FAILED);
            job.setStatusMessage(e.getMessage());
            job.setCompletedAt(OffsetDateTime.now());
            jobRepository.save(job);
            log.info("Job: [{}] FAILED", jobUuid);
        }

        return job;
    }

    /**
     * Process the Job and put the contents into the output directory
     *
     * @param job - the job to process
     * @param outputDirPath - the output directory to put all the files
     */
    private void processJob(Job job, Path outputDirPath) throws FileNotFoundException {
        // Get the output directory
        createOutputDirectory(outputDirPath);

        // Get all attested contracts for that job (or the one specified in the job)
        var attestedContracts = getAttestedContracts(job);
        var jobUuid = job.getJobUuid();

        // Retrieve the patients for each contract and start a progress tracker
        var progressTracker = initializeProgressTracker(jobUuid, attestedContracts);

        for (Contract contract : attestedContracts) {
            log.info("Job [{}] - contract [{}] ", jobUuid, contract.getContractNumber());

            // Determine the type of output
            FileOutputType outputType =  NDJSON;
            if (job.getOutputFormat() != null && job.getOutputFormat().equalsIgnoreCase(ZIPFORMAT)) {
                outputType = ZIP;
            }

            // Create a holder for the contract, writer, progress tracker and attested date
            var contractData = new ContractData(contract, progressTracker, contract.getAttestedOn(), job.getSince());

            /*** process contract ***/
            final Segment contractSegment = NewRelic.getAgent().getTransaction().startSegment("Patient processing of contract " + contract.getContractNumber());
            var jobOutputs = processContract(outputDirPath, contractData, outputType);
            contractSegment.end();

            // For each job output, add to the job and save the result
            jobOutputs.forEach(jobOutput -> job.addJobOutput(jobOutput));
            jobOutputRepository.saveAll(jobOutputs);
        }

        completeJob(job);
    }

    /**
     * Given a path to a directory, create it. If it already exists, delete it and its contents and recreate it
     *
     * @param outputDirPath - the path to the output directory you want to create
     * @return the path to the newly created directory
     */
    private Path createOutputDirectory(Path outputDirPath) {
        Path directory = null;
        try {
            directory = fileService.createDirectory(outputDirPath);
        } catch (UncheckedIOException e) {
            final IOException cause = e.getCause();
            if (cause != null && cause.getMessage().equalsIgnoreCase("Directory already exists")) {
                log.warn("Directory already exists. Delete and create afresh ...");
                deleteExistingDirectory(outputDirPath);
                directory = fileService.createDirectory(outputDirPath);
            } else {
                throw e;
            }
        }

        log.info("Created job output directory: {}", directory.toAbsolutePath());
        return directory;
    }

    /**
     * Delete directory with all the ndjson files or zip files. If the files are directories or symbolic links, write
     * error, but continue. If it's a regular file, delete it, then delete the directory. If the directory is not
     * empty, throws an exception (if it has files other than ndjson or zip in it)
     *
     * @param outputDirPath - the directory to delete
     */
    private void deleteExistingDirectory(Path outputDirPath) {
        final File[] files = outputDirPath.toFile().listFiles(getFilenameFilter());

        for (File file : files) {
            final Path filePath = file.toPath();
            if (file.isDirectory() || Files.isSymbolicLink(filePath)) {
                var errMsg = "File is not a regular file";
                log.error("{} - isDirectory: {}", errMsg, file.isDirectory());
                continue;
            }

            if (Files.isRegularFile(filePath)) {
                doDelete(filePath);
            }
        }

        doDelete(outputDirPath);
    }

    /**
     *
     * @return a Filename filter for ndjson and zip files
     */
    private FilenameFilter getFilenameFilter() {
        return (dir, name) -> {
            final String filename = name.toLowerCase();
            final String ndjson = NDJSON.getSuffix();
            final String zip = ZIP.getSuffix();
            return filename.endsWith(ndjson) || filename.endsWith(zip);
        };
    }

    private void doDelete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            var errMsg = "Could not delete ";
            log.error("{} : {} ", errMsg, path.toAbsolutePath());
            throw new UncheckedIOException(errMsg + path.toFile().getName(), ex);
        }
    }

    /**
     * Return the list of attested contracts for a job. If a contract was specified in the job, just return that
     * after checking to make sure the sponsor has access to the contract, otherwise, search for all the contracts
     * for the sponsor
     *
     * @param job - the submitted job
     * @return the list of contracts (all or only 1 if the contract was specified in the job).
     */
    private List<Contract> getAttestedContracts(Job job) {

        // Get the aggregated attested Contracts for the sponsor
        final Sponsor sponsor = job.getUser().getSponsor();
        final List<Contract> attestedContracts = sponsor.getAggregatedAttestedContracts();

        // If a contract was specified for request, make sure the sponsor can access the contract and then return only it
        final Contract jobSpecificContract = job.getContract();
        if (jobSpecificContract != null && jobSpecificContract.getAttestedOn() != null) {
            boolean ownsContract = attestedContracts.stream()
                    .anyMatch(c -> jobSpecificContract.getContractNumber().equalsIgnoreCase(c.getContractNumber()));
            if (!ownsContract) {
                log.info("Job [{}] submitted for a specific attested contract [{}] that the sponsor [{}] does not own",
                        job.getJobUuid(), jobSpecificContract.getContractNumber(), sponsor.getOrgName());
            }
            log.info("Job [{}] submitted for a specific attested contract [{}] ", job.getJobUuid(), jobSpecificContract.getContractNumber());
            return Collections.singletonList(jobSpecificContract);
        }

        // Otherwise, return the list of attested contracts
        log.info("Job [{}] has [{}] attested contracts", job.getJobUuid(), attestedContracts.size());
        return attestedContracts;
    }


    /**
     * Creates a ProgressTracker for the list of all patients in all contracts
     *
     * @param attestedContracts - the list of attested contracts
     * @return the progress tracker
     */
    private ProgressTracker initializeProgressTracker(String jobUuid, List<Contract> attestedContracts) {
        return ProgressTracker.builder()
                .jobUuid(jobUuid)
                .failureThreshold(failureThreshold)
                .patientsByContracts(fetchPatientsForAllContracts(attestedContracts))
                .build();
    }

    /**
     * Calls the BB contract adaptor and creates a list patients for each contract
     *
     * @param attestedContracts - the attested contracts
     * @return the list of patients for each contract
     */
    private List<GetPatientsByContractResponse> fetchPatientsForAllContracts(List<Contract> attestedContracts) {
        int currentMonth = LocalDate.now().getMonthValue();
        return attestedContracts
                .stream()
                .map(contract -> contract.getContractNumber())
                .map(contractNumber -> contractAdapter.getPatients(contractNumber, currentMonth))
                .collect(Collectors.toList());
    }

    /**
     * Return the number of bytes when to rollover given the number of megabytes in a zip file if used
     *
     * @return the number of bytes
     */
    private long getZipRolloverThreshold() {
        return zipRollOver * Constants.ONE_MEGA_BYTE;
    }

    /**
     * Return the number of bytes when to rollover given the number of megabytes
     *
     * @return the number of bytes
     */
    private long getRollOverThreshold() {
        return ndjsonRollOver * Constants.ONE_MEGA_BYTE;
    }


    /**
     * Process the contract - retrieve all the patients for the contract and create a thread in the
     * patientProcessorThreadPool to handle searching for EOBs for each patient. Periodically check to
     * see if the job is cancelled and cancel the threads if necessary, otherwise, wait until all threads
     * have processed.
     *
     * @param contractData - the contract data (contract, progress tracker, attested time, writer)
     * @return - the job output records containing the file information
     */
    private List<JobOutput> processContract(Path outputDirPath, ContractData contractData,
                                            FileOutputType contractType) throws FileNotFoundException {
        var contract = contractData.getContract();
        log.info("Beginning to process contract {}", keyValue(CONTRACT_LOG, contract.getContractName()));

        var contractNumber = contract.getContractNumber();

        var progressTracker = contractData.getProgressTracker();

        var patientsByContract = getPatientsByContract(contractNumber, progressTracker);
        var patients = patientsByContract.getPatients();
        int patientCount = patients.size();
        log.info("Contract [{}] has [{}] Patients", contractNumber, patientCount);

        boolean isCancelled = false;

        StreamHelper helper = null;
        try {
            if (contractType == ZIP) {
                helper = new ZipStreamHelperImpl(outputDirPath, contractNumber, getZipRolloverThreshold(), getRollOverThreshold(), tryLockTimeout);
            } else {
                helper = new TextStreamHelperImpl(outputDirPath, contractNumber, getRollOverThreshold(), tryLockTimeout);
            }
            int recordsProcessedCount = 0;
            var futureHandles = new ArrayList<Future<Void>>();
            for (PatientDTO patient : patients) {
                ++recordsProcessedCount;

                final String patientId = patient.getPatientId();

                if (isOptOutPatient(patientId)) {
                    // this patient has opted out. skip patient record.
                    continue;
                }

                // Add the thread to process the patient and start the thread

                // See https://docs.newrelic.com/docs/agents/java-agent/async-instrumentation/java-agent-api-asynchronous-applications
                // for more detail on tokens with async calls
                final Token token = NewRelic.getAgent().getTransaction().getToken();

                // Using a ThreadLocal to communicate contract number to RoundRobinBlockingQueue
                // could be viewed as a hack by many; but on the other hand it saves us from writing
                // tons of extra code.
                RoundRobinBlockingQueue.CATEGORY_HOLDER.set(progressTracker.getJobUuid());
                try {
                    futureHandles.add(patientClaimsProcessor
                            .process(patient, helper, contract.getAttestedOn(), contractData.getSinceTime(), token));
                } finally {
                    RoundRobinBlockingQueue.CATEGORY_HOLDER.remove();
                }

                // Periodically check if cancelled
                if (recordsProcessedCount % cancellationCheckFrequency == 0) {

                    var jobUuid = contractData.getProgressTracker().getJobUuid();
                    isCancelled = hasJobBeenCancelled(jobUuid);
                    if (isCancelled) {
                        log.warn("Job [{}] has been cancelled. Attempting to stop processing the job shortly ... ", jobUuid);
                        cancelFuturesInQueue(futureHandles);
                        break;
                    }

                    processHandles(futureHandles, progressTracker);
                }
            }

            // While there are still patients not being processed, sleep for a bit and check progress
            while (!futureHandles.isEmpty()) {
                sleep();
                processHandles(futureHandles, progressTracker);
            }

            if (isCancelled) {
                final String errMsg = "Job was cancelled while it was being processed";
                log.warn("{}", errMsg);
                throw new JobCancelledException(errMsg);
            }
        } finally {
            if (helper != null) {
                try {
                    helper.close();
                } catch (Exception ex) {
                    log.error("Unable to close the helper", ex);
                }
            }
        }
        // All jobs are done, return the job output records
        return createJobOutputs(helper.getDataFiles(), helper.getErrorFiles());
    }

    /**
     * Retrieve the patients by contract
     *
     * @param contractNumber - the contract number
     * @param progressTracker - the progress tracker for all contracts and patients for the job
     * @return the contract's patients
     */
    private GetPatientsByContractResponse getPatientsByContract(String contractNumber, ProgressTracker progressTracker) {
        return progressTracker.getPatientsByContracts()
                .stream()
                .filter(byContract -> byContract.getContractNumber().equals(contractNumber))
                .findFirst()
                .orElse(null);
    }

    /**
     * Cancel threads
     *
     * @param futureHandles - the running threads
     */
    private void cancelFuturesInQueue(List<Future<Void>> futureHandles) {

        // cancel any futures that have not started processing and are waiting in the queue.
        futureHandles.parallelStream().forEach(future -> future.cancel(false));

        //At this point, there may be a few futures that are already in progress.
        //But all the futures that are not yet in progress would be cancelled.
    }

    /**
     * A Job could run for a long time, perhaps hours. An in process job can be cancelled. Here
     * we search for the job and determine if the status has been changed to cancel. This is checked
     * periodically while processing the job.
     *
     * @param jobUuid - the job id
     * @return true if the job is cancelled
     */
    private boolean hasJobBeenCancelled(String jobUuid) {
        final JobStatus jobStatus = jobRepository.findJobStatus(jobUuid);
        return CANCELLED.equals(jobStatus);
    }

    /**
     * Check the opt out repository to see if a patient has opted out of data services
     *
     * @param patientId - the patient id
     * @return true if the patient has opted out
     */
    private boolean isOptOutPatient(String patientId) {

        final List<OptOut> optOuts = optOutRepository.findByCcwId(patientId);
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

    /**
     * For each future, check to see if it's done. If it is, remove it from the list of future handles
     * and increment the number processed
     *
     * @param futureHandles - the thread futures
     * @param progressTracker - the tracker with updated tracker information
     */
    private void processHandles(List<Future<Void>> futureHandles, ProgressTracker progressTracker) {
        var iterator = futureHandles.iterator();
        while (iterator.hasNext()) {
            var future = iterator.next();
            if (future.isDone()) {
                processFuture(futureHandles, progressTracker, future);
                iterator.remove();
            }
        }

        // update the progress in the DB & logs periodically
        trackProgress(progressTracker);
    }

    /**
     * process the future that is marked as done.
     * On doing a get(), if an exception is thrown, analyze it to decide whether to stop the batch or not.
     * @param futureHandles
     * @param progressTracker
     * @param future
     */
    private void processFuture(List<Future<Void>> futureHandles, ProgressTracker progressTracker, Future<Void> future) {
        progressTracker.incrementProcessedCount();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            analyzeException(futureHandles, progressTracker, e);

        } catch (CancellationException e) {
            // This could happen in the rare event that a job was cancelled mid-process.
            // due to which the futures in the queue (that were not yet in progress) were cancelled.
            // Nothing to be done here
            log.warn("CancellationException while calling Future.get() - Job may have been cancelled");
        }
    }

    private void analyzeException(List<Future<Void>> futureHandles, ProgressTracker progressTracker, Exception e) {
        progressTracker.incrementFailureCount();

        if (progressTracker.isErrorCountBelowThreshold()) {
            final Throwable rootCause = ExceptionUtils.getRootCause(e);
            log.error("exception while processing patient {}", rootCause.getMessage(), rootCause);
            // log exception, but continue processing job as errorCount is below threshold
        } else {
            cancelFuturesInQueue(futureHandles);
            log.error("{} out of {} records failed. Stopping job", progressTracker.getFailureCount(), progressTracker.getTotalCount());
            throw new RuntimeException("Too many patient records in the job had failures");
        }
    }

    /**
     * Update the database or log with the % complete on the job periodically
     *
     * @param progressTracker - the progress tracker
     */

    private void trackProgress(ProgressTracker progressTracker) {
        if (progressTracker.isTimeToUpdateDatabase(reportProgressDbFrequency)) {
            final int percentageCompleted = progressTracker.getPercentageCompleted();

            if (percentageCompleted > progressTracker.getLastUpdatedPercentage()) {
                jobRepository.updatePercentageCompleted(progressTracker.getJobUuid(), percentageCompleted);
                progressTracker.setLastUpdatedPercentage(percentageCompleted);
            }
        }

        var processedCount = progressTracker.getProcessedCount();
        if (progressTracker.isTimeToLog(reportProgressLogFrequency)) {
            progressTracker.setLastLogUpdateCount(processedCount);

            var totalCount = progressTracker.getTotalCount();
            var percentageCompleted = progressTracker.getPercentageCompleted();
            log.info("[{}/{}] records processed = [{}% completed]", processedCount, totalCount, percentageCompleted);
        }
    }

    /**
     * Once the job writer is finished, create a list of job output objects with
     * the data files and the error files
     *
     * @param dataFiles - the results of writing the contract
     * @param errorFiles - any errors that arose due to writing the contract
     * @return the list of job output objects
     */
    private List<JobOutput> createJobOutputs(List<Path> dataFiles, List<Path> errorFiles) {

        // create Job Output records for data files from the job writer
        final List<JobOutput> jobOutputs = dataFiles.stream()
                .map(dataFile -> createJobOutput(dataFile, false)).collect(Collectors.toList());

        // create Job Output record for error file
        final List<JobOutput> errorJobOutputs = errorFiles.stream()
                .map(errorFile -> createJobOutput(errorFile, true))
                .collect(Collectors.toList());
        jobOutputs.addAll(errorJobOutputs);

        if (jobOutputs.isEmpty()) {
            var errMsg = "The export process has produced no results";
            throw new RuntimeException(errMsg);
        }

        return jobOutputs;
    }

    /**
     * From a file, return the JobOutput object
     *
     * @param outputFile - the output file from the job
     * @param isError - if there was an error
     * @return - the joub output object
     */
    private JobOutput createJobOutput(Path outputFile, boolean isError) {
        JobOutput jobOutput = new JobOutput();
        jobOutput.setFilePath(outputFile.getFileName().toString());
        jobOutput.setFhirResourceType(EOB);
        jobOutput.setError(isError);
        jobOutput.setChecksum(fileService.generateChecksum(outputFile.toFile()));
        jobOutput.setFileLength(outputFile.toFile().length());
        return jobOutput;
    }

    /**
     * Set the job as complete in the database
     *
     * @param job - The job to set as complete
     */
    private void completeJob(Job job) {
        job.setStatus(SUCCESSFUL);
        job.setStatusMessage("100%");
        job.setProgress(100);
        job.setExpiresAt(OffsetDateTime.now().plusHours(auditFilesTTLHours));
        job.setCompletedAt(OffsetDateTime.now());

        jobRepository.save(job);
        log.info("Job: [{}] is DONE", job.getJobUuid());
    }

    /**
     * Tell the thread to sleep
     */
    private void sleep() {
        try {
            Thread.sleep(SLEEP_DURATION);
        } catch (InterruptedException e) {
            log.warn("interrupted exception in thread.sleep(). Ignoring");
        }
    }
}
