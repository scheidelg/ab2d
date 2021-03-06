package gov.cms.ab2d.worker.stuckjob;

import gov.cms.ab2d.common.model.Job;
import gov.cms.ab2d.common.model.JobStatus;
import gov.cms.ab2d.common.repository.JobRepository;
import gov.cms.ab2d.eventlogger.LogManager;
import gov.cms.ab2d.eventlogger.events.JobStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelStuckJobsProcessorImpl implements CancelStuckJobsProcessor {

    @Value("${stuck.job.cancel.threshold}")
    private int cancelThreshold;

    private final JobRepository jobRepository;
    private final LogManager eventLogger;

    @Override
    @Transactional
    public void process() {

        final OffsetDateTime cutOffTime = OffsetDateTime.now().minusHours(cancelThreshold);

        final List<Job> stuckJobs = jobRepository.findStuckJobs(cutOffTime);
        log.info("Found {} jobs that appears to be stuck", stuckJobs.size());

        for (Job stuckJob : stuckJobs) {
            log.warn("Cancelling job - uuid:{} - created at:{} - status :{} - completed_at",
                    stuckJob.getJobUuid(), stuckJob.getCreatedAt(), stuckJob.getStatus(), stuckJob.getCompletedAt());

            eventLogger.log(new JobStatusChangeEvent(
                    stuckJob.getUser() == null ? null : stuckJob.getUser().getUsername(),
                    stuckJob.getJobUuid(),
                    stuckJob.getStatus() == null ? null : stuckJob.getStatus().name(),
                    JobStatus.CANCELLED.name(), "Cancelling stuck job"));
            stuckJob.setStatus(JobStatus.CANCELLED);
            jobRepository.save(stuckJob);
        }
    }
}
