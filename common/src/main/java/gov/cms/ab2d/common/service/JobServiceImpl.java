package gov.cms.ab2d.common.service;


import gov.cms.ab2d.common.repository.JobRepository;
import gov.cms.ab2d.common.model.Job;
import gov.cms.ab2d.common.model.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private UserService userService;

    @Autowired
    private JobRepository jobRepository;

    public static final String INITIAL_JOB_STATUS_MESSAGE = "0%";

    public Job createJob(String resourceTypes, String url) {
        Job job = new Job();
        job.setResourceTypes(resourceTypes);
        job.setJobID(UUID.randomUUID().toString());
        job.setRequestURL(url);
        job.setStatus(JobStatus.SUBMITTED);
        job.setStatusMessage(INITIAL_JOB_STATUS_MESSAGE);
        job.setCreatedAt(OffsetDateTime.now());
        job.setProgress(0);
        job.setUser(userService.getCurrentUser());

        return jobRepository.save(job);
    }

    public void cancelJob(String jobID) {
        Job job = getJobByJobID(jobID);

        if (!job.getStatus().isCancellable()) {
            throw new InvalidJobStateTransition("Job has a status of " + job.getStatus() + ", so it cannot be cancelled");
        }

        jobRepository.cancelJobByJobID(jobID);
    }

    public Job getJobByJobID(String jobID) {
        Job job = jobRepository.findByJobID(jobID);
        if (job == null) {
            throw new ResourceNotFoundException("No job with jobID " +  jobID + " was found");
        }

        return job;
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }
}