package gov.cms.ab2d.common.service;

import gov.cms.ab2d.common.SpringBootApp;
import gov.cms.ab2d.common.model.*;
import gov.cms.ab2d.common.repository.*;
import gov.cms.ab2d.common.util.AB2DPostgresqlContainer;
import gov.cms.ab2d.common.util.DataSetup;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionSystemException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gov.cms.ab2d.common.service.JobServiceImpl.INITIAL_JOB_STATUS_MESSAGE;
import static gov.cms.ab2d.common.util.Constants.EOB;
import static gov.cms.ab2d.common.util.Constants.OPERATION_OUTCOME;
import static gov.cms.ab2d.common.util.DataSetup.TEST_USER;
import static gov.cms.ab2d.common.util.DataSetup.VALID_CONTRACT_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SpringBootApp.class)
@TestPropertySource(locations = "/application.common.properties")
@Testcontainers
public class JobServiceTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private JobOutputRepository jobOutputRepository;

    @Autowired
    private DataSetup dataSetup;

    @Autowired
    private UserService userService;

    @Value("${efs.mount}")
    private String tmpJobLocation;

    @Autowired
    private RoleService roleService;

    @Container
    private static final PostgreSQLContainer postgreSQLContainer= new AB2DPostgresqlContainer();

    // Be safe and make sure nothing from another test will impact current test
    @BeforeEach
    public void setup() {
        contractRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();
        sponsorRepository.deleteAll();

        dataSetup.setupUser(List.of());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(TEST_USER,
                                "test", new ArrayList<>()), "pass"));
    }

    @Test
    public void createJob() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");
        assertThat(job).isNotNull();
        assertThat(job.getId()).isNotNull();
        assertThat(job.getJobUuid()).isNotNull();
        assertEquals(job.getProgress(), Integer.valueOf(0));
        assertEquals(job.getUser(), userRepository.findByUsername(TEST_USER));
        assertEquals(job.getResourceTypes(), EOB);
        assertEquals(job.getRequestUrl(), "http://localhost:8080");
        assertEquals(job.getStatusMessage(), INITIAL_JOB_STATUS_MESSAGE);
        assertEquals(job.getStatus(), JobStatus.SUBMITTED);
        assertEquals(job.getJobOutputs().size(), 0);
        assertEquals(job.getLastPollTime(), null);
        assertEquals(job.getExpiresAt(), null);
        assertThat(job.getJobUuid()).matches(
                "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

        // Verify it actually got persisted in the DB
        assertThat(jobRepository.findById(job.getId())).get().isEqualTo(job);
    }

    @Test
    public void createJobWithContract() {
        Contract contract = contractRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).iterator().next();

        Job job = jobService.createJob(EOB, "http://localhost:8080", contract.getContractNumber());
        assertThat(job).isNotNull();
        assertThat(job.getId()).isNotNull();
        assertThat(job.getJobUuid()).isNotNull();
        assertEquals(job.getProgress(), Integer.valueOf(0));
        assertEquals(job.getUser(), userRepository.findByUsername(TEST_USER));
        assertEquals(job.getResourceTypes(), EOB);
        assertEquals(job.getRequestUrl(), "http://localhost:8080");
        assertEquals(job.getStatusMessage(), INITIAL_JOB_STATUS_MESSAGE);
        assertEquals(job.getStatus(), JobStatus.SUBMITTED);
        assertEquals(job.getJobOutputs().size(), 0);
        assertEquals(job.getLastPollTime(), null);
        assertEquals(job.getExpiresAt(), null);
        assertThat(job.getJobUuid()).matches(
                "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
        assertEquals(job.getContract().getContractNumber(), contract.getContractNumber());

        // Verify it actually got persisted in the DB
        assertThat(jobRepository.findById(job.getId())).get().isEqualTo(job);
    }

    @Test
    public void createJobWithBadContract() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            jobService.createJob(EOB, "http://localhost:8080", "BadContract");
        });
    }

    @Test
    public void failedValidation() {
        Assertions.assertThrows(TransactionSystemException.class, () -> {
            jobService.createJob("Patient,ExplanationOfBenefit,Coverage", "http://localhost:8080");
        });
    }

    @Test
    public void cancelJob() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");

        jobService.cancelJob(job.getJobUuid());

        // Verify that it has the correct status
        Job cancelledJob = jobRepository.findByJobUuid(job.getJobUuid());

        assertEquals(JobStatus.CANCELLED, cancelledJob.getStatus());
    }

    @Test
    public void cancelNonExistingJob() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            jobService.cancelJob("NonExistingJob");
        });
    }

    @Test
    public void getJob() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");

        Job retrievedJob = jobService.getAuthorizedJobByJobUuid(job.getJobUuid());

        assertEquals(job, retrievedJob);
    }

    @Test
    public void getNonExistentJob() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getAuthorizedJobByJobUuid("NonExistent");
        });
    }

    @Test
    public void testJobInSuccessfulState() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");

        job.setStatus(JobStatus.SUCCESSFUL);
        jobRepository.saveAndFlush(job);

        Assertions.assertThrows(InvalidJobStateTransition.class, () -> {
            jobService.cancelJob(job.getJobUuid());
        });
    }

    @Test
    public void testJobInCancelledState() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");

        job.setStatus(JobStatus.CANCELLED);
        jobRepository.saveAndFlush(job);

        Assertions.assertThrows(InvalidJobStateTransition.class, () -> {
            jobService.cancelJob(job.getJobUuid());
        });

    }

    @Test
    public void testJobInFailedState() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");

        job.setStatus(JobStatus.FAILED);
        jobRepository.saveAndFlush(job);

        Assertions.assertThrows(InvalidJobStateTransition.class, () -> {
            jobService.cancelJob(job.getJobUuid());
        });

    }

    @Test
    public void updateJob() {
        Job job = jobService.createJob(EOB, "http://localhost:8080");
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime localDateTime = OffsetDateTime.now();
        job.setProgress(100);
        job.setLastPollTime(now);
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setCreatedAt(localDateTime);
        job.setCompletedAt(localDateTime);
        job.setJobUuid("abc");
        job.setResourceTypes(EOB);
        job.setRequestUrl("http://localhost");
        job.setStatusMessage("Pending");
        job.setExpiresAt(now);

        JobOutput jobOutput = new JobOutput();
        jobOutput.setError(false);
        jobOutput.setFhirResourceType(EOB);
        jobOutput.setFilePath("file.ndjson");
        jobOutput.setJob(job);

        JobOutput errorJobOutput = new JobOutput();
        errorJobOutput.setError(true);
        errorJobOutput.setFhirResourceType(OPERATION_OUTCOME);
        errorJobOutput.setFilePath("error.ndjson");
        errorJobOutput.setJob(job);

        List<JobOutput> output = List.of(jobOutput, errorJobOutput);
        job.setJobOutputs(output);

        Job updatedJob = jobService.updateJob(job);
        Assert.assertEquals(Integer.valueOf(100), updatedJob.getProgress());
        Assert.assertEquals(now, updatedJob.getLastPollTime());
        Assert.assertEquals(JobStatus.IN_PROGRESS, updatedJob.getStatus());
        Assert.assertEquals(localDateTime, updatedJob.getCreatedAt());
        Assert.assertEquals(localDateTime, updatedJob.getCompletedAt());
        Assert.assertEquals("abc", updatedJob.getJobUuid());
        Assert.assertEquals(EOB, updatedJob.getResourceTypes());
        Assert.assertEquals("http://localhost", updatedJob.getRequestUrl());
        Assert.assertEquals("Pending", updatedJob.getStatusMessage());
        Assert.assertEquals(now, updatedJob.getExpiresAt());

        JobOutput updatedOutput = updatedJob.getJobOutputs().get(0);
        Assert.assertEquals(false, updatedOutput.getError());
        Assert.assertEquals(EOB, updatedOutput.getFhirResourceType());
        Assert.assertEquals("file.ndjson", updatedOutput.getFilePath());

        JobOutput updatedErrorOutput = updatedJob.getJobOutputs().get(1);
        Assert.assertEquals(true, updatedErrorOutput.getError());
        Assert.assertEquals(OPERATION_OUTCOME, updatedErrorOutput.getFhirResourceType());
        Assert.assertEquals("error.ndjson", updatedErrorOutput.getFilePath());
    }

    @Test
    public void getFileDownloadUrl() throws IOException {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);

        Path destination = Paths.get(tmpJobLocation, job.getJobUuid());
        String destinationStr = destination.toString();
        Files.createDirectories(destination);

        createNDJSONFile(testFile, destinationStr);
        createNDJSONFile(errorFile, destinationStr);

        Resource resource = jobService.getResourceForJob(job.getJobUuid(), testFile);
        Assert.assertEquals(testFile, resource.getFilename());

        Resource errorResource = jobService.getResourceForJob(job.getJobUuid(), errorFile);
        Assert.assertEquals(errorFile, errorResource.getFilename());
    }

    @Test
    public void getJobOutputFromDifferentUser() throws IOException {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);

        Path destination = Paths.get(tmpJobLocation, job.getJobUuid());
        String destinationStr = destination.toString();
        Files.createDirectories(destination);

        createNDJSONFile(testFile, destinationStr);
        createNDJSONFile(errorFile, destinationStr);

        User user = new User();
        Role role = roleService.findRoleByName("SPONSOR");
        user.setRoles(Set.of(role));
        user.setUsername("BadUser");
        user.setEnabled(true);

        Sponsor savedSponsor = dataSetup.createSponsor("Parent Corp. #2", 12345, "Test #2", 6789);

        dataSetup.setupContract(savedSponsor, "New Contract");

        user.setSponsor(savedSponsor);
        User savedUser = userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(savedUser.getUsername(),
                                "test", new ArrayList<>()), "pass"));

        var exceptionThrown = assertThrows(
                InvalidJobAccessException.class,
                () -> jobService.getResourceForJob(job.getJobUuid(), testFile));

        Assert.assertEquals(exceptionThrown.getMessage(), "Unauthorized");
    }

    private void createNDJSONFile(String file, String destinationStr) throws IOException {
        InputStream testFileStream = this.getClass().getResourceAsStream("/" + file);
        String fileStr = IOUtils.toString(testFileStream, "UTF-8");
        try (PrintWriter out = new PrintWriter(destinationStr + File.separator + file)) {
            out.println(fileStr);
        }
    }

    private Job createJobForFileDownloads(String fileName, String errorFileName) {
        Job job = jobService.createJob(EOB, "http://localhost:8080");
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime localDateTime = OffsetDateTime.now();
        job.setProgress(100);
        job.setLastPollTime(now);
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setCreatedAt(localDateTime);
        job.setCompletedAt(localDateTime);
        job.setResourceTypes(EOB);
        job.setRequestUrl("http://localhost");
        job.setStatusMessage("Pending");
        job.setExpiresAt(now);
        job.setUser(userService.getCurrentUser());

        JobOutput jobOutput = new JobOutput();
        jobOutput.setError(false);
        jobOutput.setFhirResourceType(EOB);
        jobOutput.setFilePath(fileName);
        jobOutput.setJob(job);

        JobOutput errorJobOutput = new JobOutput();
        errorJobOutput.setError(true);
        errorJobOutput.setFhirResourceType(OPERATION_OUTCOME);
        errorJobOutput.setFilePath(errorFileName);
        errorJobOutput.setJob(job);

        List<JobOutput> output = List.of(jobOutput, errorJobOutput);
        job.setJobOutputs(output);

        return jobService.updateJob(job);
    }

    @Test
    public void getFileDownloadUrlWithWrongFilename() {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getResourceForJob(job.getJobUuid(), "filenamewrong.ndjson");
        });

    }

    @Test
    public void getFileDownloadUrlWitMissingOutput() {
        String testFile = "outputmissing.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);

        Assertions.assertThrows(JobOutputMissingException.class, () -> {
            jobService.getResourceForJob(job.getJobUuid(), "outputmissing.ndjson");
        });
    }

    @Test
    public void getFileDownloadAlreadyDownloaded() {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);
        JobOutput jobOutput = job.getJobOutputs().iterator().next();
        jobOutput.setDownloaded(true);
        jobOutputRepository.save(jobOutput);


        var exception = Assertions.assertThrows(JobOutputMissingException.class, () -> {
            jobService.getResourceForJob(job.getJobUuid(), "test.ndjson");
        });
        Assert.assertThat(exception.getMessage(), is("The file is not present as it has already been downloaded. Please resubmit the job."));
    }

    @Test
    public void getFileDownloadExpired() {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);
        job.setExpiresAt(OffsetDateTime.now().minusDays(2));
        jobRepository.save(job);


        var exception = Assertions.assertThrows(JobOutputMissingException.class, () -> {
            jobService.getResourceForJob(job.getJobUuid(), "test.ndjson");
        });
        Assert.assertThat(exception.getMessage(), is("The file is not present as it has expired. Please resubmit the job."));
    }

    @Test
    public void checkIfUserCanAddJobTest() {
        boolean result = jobService.checkIfCurrentUserCanAddJob();
        Assert.assertTrue(result);
    }

    @Test
    public void checkIfUserCanAddJobTrueTest() {
        jobService.createJob(EOB, "http://localhost:8080");

        boolean result = jobService.checkIfCurrentUserCanAddJob();
        Assert.assertTrue(result);
    }

    @Test
    public void checkIfUserCanAddJobPastLimitTest() {
        jobService.createJob(EOB, "http://localhost:8080");
        jobService.createJob(EOB, "http://localhost:8080");
        jobService.createJob(EOB, "http://localhost:8080");

        boolean result = jobService.checkIfCurrentUserCanAddJob();
        Assert.assertFalse(result);
    }

    @Test
    public void deleteFileForJobTest() {
        String testFile = "test.ndjson";
        String errorFile = "error.ndjson";
        Job job = createJobForFileDownloads(testFile, errorFile);
        jobService.deleteFileForJob(new File(testFile), job.getJobUuid());
    }
}
