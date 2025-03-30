package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.repo.JobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@EnableScheduling
public class JobStatusScheduler {
    @Autowired
    private JobRepo jobRepository;

    @Transactional
    @Scheduled(cron = "0 0 6 * * ?") // Runs every day at 6:00 AM
    public void updateJobStatuses() {
        Date currentDate = new Date(); // Get current date-time

        // Update jobs that should be ON_GOING
        List<Job> ongoingJobs = jobRepository.findByStartDateBeforeAndJobStatusNot(currentDate, JobStatus.ON_GOING);
        for (Job job : ongoingJobs) {
            job.setJobStatus(JobStatus.ON_GOING);
        }

        // Update jobs that should be FINISHED
        List<Job> finishedJobs = jobRepository.findByEndDateBeforeAndJobStatusNot(currentDate, JobStatus.FINISH);
        for (Job job : finishedJobs) {
            job.setJobStatus(JobStatus.FINISH);
        }

        // Save all changes in one transaction (performance boost)
        jobRepository.saveAll(ongoingJobs);
        jobRepository.saveAll(finishedJobs);
    }
}
