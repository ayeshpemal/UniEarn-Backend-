package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedCategoryStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobSummeryDTO;
import com.finalproject.uni_earn.dto.Response.*;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.UnauthorizedActionException;
import com.finalproject.uni_earn.exception.UserNotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.EmployerAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EmployerAnalysisServiceIMPL implements EmployerAnalysisService {

    private final JobRepo jobRepo;
    private final ApplicationRepo applicationRepo;
    private final UserRepo userRepo;

    @Autowired
    public EmployerAnalysisServiceIMPL(JobRepo jobRepo, ApplicationRepo applicationRepo, UserRepo userRepo) {
        this.jobRepo = jobRepo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
    }

    private void verifyEmployerExists(long employerId) {
        Optional<User> userOptional = userRepo.findByUserId(employerId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with ID " + employerId + " not found");
        }

        User user = userOptional.get();
        if (user.getRole() != Role.EMPLOYER) {
            throw new UnauthorizedActionException("User with ID " + employerId + " is not an employer");
        }
    }

    /*
     * this for getting all the job summery for the employer,it is paginated, and it will return the total number of jobs
     * */
    @Override
    public PaginatedJobSummeryDTO getAllJobSummeryForEmployer(long employerId, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobSummeryDetails> jobSummeryDetails = jobRepo.getJobSummeryDetails(employerId, pageRequest);
        return new PaginatedJobSummeryDTO(jobSummeryDetails.getContent(), jobRepo.countByEmployer_UserId(employerId));
    }

    /*
     * This for getting all job summaries for the employer filtered by date range,
     * it is paginated, and it will return the total number of jobs in that date range
     */

    @Override
    public PaginatedJobSummeryDTO getJobsByEmployerAndDateRange(long employerId, Date startDate, Date endDate, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobSummeryDetails> jobSummeryDetails = jobRepo.getJobsByEmployerAndDateRange(employerId, startDate, endDate, pageRequest);
        return new PaginatedJobSummeryDTO(
                jobSummeryDetails.getContent(),
                jobRepo.countJobsByEmployerAndDateRange(employerId, startDate, endDate)
        );


    }

    /*
     * This for getting all job summaries for the employer filtered by job status,
     * it is paginated, and it will return the total number of jobs with that status
     */

    @Override
    public PaginatedJobSummeryDTO getJobsByEmployerAndStatus(long employerId, JobStatus jobStatus, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobSummeryDetails> jobSummeryDetails = jobRepo.getJobsByEmployerAndStatus(employerId, jobStatus, pageRequest);
        return new PaginatedJobSummeryDTO(
                jobSummeryDetails.getContent(),
                jobRepo.countJobsByEmployerAndStatus(employerId, jobStatus)
        );
    }

    /*
     * This for getting all job summaries for the employer filtered by both job status and date range,
     * it is paginated, and it will return the total number of jobs matching both criteria
     */

    @Override
    public PaginatedJobSummeryDTO getJobsByEmployerStatusAndDateRange(long employerId, JobStatus jobStatus, Date startDate, Date endDate, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobSummeryDetails> jobSummeryDetails = jobRepo.getJobsByEmployerStatusAndDateRange(employerId, jobStatus, startDate, endDate, pageRequest);
        return new PaginatedJobSummeryDTO(
                jobSummeryDetails.getContent(),
                jobRepo.countJobsByEmployerStatusAndDateRange(employerId, jobStatus, startDate, endDate)
        );


    }

    /*
     * This returns jobs with the most applications for the employer,
     * it is paginated, and returns the total number of jobs with applications
     */

    @Override
    public PaginatedJobStaticsDTO getJobsWithMostApplicationsByEmployerId(long employerId, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobStaticsDTO> jobStaticsDTOS = applicationRepo.findJobsWithMostApplicationsByEmployerId(employerId, pageRequest);
        return new PaginatedJobStaticsDTO(
                jobStaticsDTOS.getContent(),
                applicationRepo.countJobsWithApplicationsByEmployerId(employerId)
        );
    }

    /*
     * This returns jobs with the least applications for the employer,
     * it is paginated, and returns the total number of jobs with applications
     */

    @Override
    public PaginatedJobStaticsDTO getJobsWithLeastApplicationsByEmployerId(long employerId, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobStaticsDTO> jobStaticsDTOS = applicationRepo.findJobsWithLeastApplicationsByEmployerId(employerId, pageRequest);
        return new PaginatedJobStaticsDTO(
                jobStaticsDTOS.getContent(),
                applicationRepo.countJobsWithApplicationsByEmployerId(employerId)
        );
    }

    /*
     * This returns the most popular job categories based on application count for the employer,
     * it is paginated, and returns the total number of job categories with applications
     */


    @Override
    public PaginatedCategoryStaticsDTO getMostPopularJobCategoriesByEmployerId(long employerId, int page, int size) {
        verifyEmployerExists(employerId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobCategoryStatisticsDTO> categoryStatistics = applicationRepo.findMostPopularJobCategoriesByEmployerId(employerId, pageRequest);
        return new PaginatedCategoryStaticsDTO(
                categoryStatistics.getContent(),
                applicationRepo.countJobCategoriesWithApplicationsByEmployerId(employerId)
        );

    }

    @Override
    public  EmployerBriefSummaryDTO getBriefSummary(Long employerId, Date startDate, Date endDate) {

        verifyEmployerExists(employerId);


        Instant instant = startDate.toInstant();
        LocalDateTime startDate_ = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        Instant instant_ = endDate.toInstant();
        LocalDateTime endDate_ = LocalDateTime.ofInstant(instant_, ZoneId.systemDefault());

        EmployerBriefSummaryDTO summary = new EmployerBriefSummaryDTO();

        //  Get total job count
        summary.setTotalJobCount(jobRepo.countByEmployer_UserIdAndDateRange(employerId, startDate_, endDate_));

        /*
        * Get counts by job status
        For each status type, get the count of jobs with that status*/
        summary.setPendingJobCount(jobRepo.countByEmployerIdAndStatusAndDateRange(employerId, JobStatus.PENDING, startDate_, endDate_));
        summary.setOngoingJobCount(jobRepo.countByEmployerIdAndStatusAndDateRange(employerId, JobStatus.ON_GOING, startDate_, endDate_));
        summary.setFinishedJobCount(jobRepo.countByEmployerIdAndStatusAndDateRange(employerId, JobStatus.FINISH, startDate_, endDate_));
        summary.setCanceledJobCount(jobRepo.countByEmployerIdAndStatusAndDateRange(employerId, JobStatus.CANCEL, startDate_, endDate_));

        /*
        Get counts by category
         This returns category and count pairs that we convert to a map
        * */
        List<Object[]> categoryStats = jobRepo.countByEmployerIdGroupByCategoryAndDateRange(employerId, startDate_, endDate_);
        Map<JobCategory, Long> categoryCounts = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryCounts.put((JobCategory) stat[0], (Long) stat[1]);
        }
        summary.setJobCountByCategory(categoryCounts);

        /*
        *  Get counts by location
        This returns location and count pairs that we convert to a map
        * */
        List<Object[]> locationStats = jobRepo.countByEmployerIdGroupByLocationAndDateRange(employerId, startDate_, endDate_);
        Map<Location, Long> locationCounts = new HashMap<>();
        for (Object[] stat : locationStats) {
            locationCounts.put((Location) stat[0], (Long) stat[1]);
        }
        summary.setJobCountByLocation(locationCounts);

//        the top 3 jobs with the most applications
        List<Object[]> mostAppliedJobsData = applicationRepo.findJobsWithMostApplicationsByEmployerIdAndDateRange(employerId, startDate_, endDate_);
        List<JobApplicationCountDTO> mostAppliedJobs = new ArrayList<>();
        for (Object[] jobData : mostAppliedJobsData) {
            mostAppliedJobs.add(new JobApplicationCountDTO(
                    (Long) jobData[0],
                    (String) jobData[1],
                    (Long) jobData[2]
            ));
        }
        summary.setMostAppliedJobs(mostAppliedJobs);

        // the top 3 jobs with the least applications
        List<Object[]> leastAppliedJobsData = applicationRepo.findJobsWithLeastApplicationsByEmployerIdAndDateRange(employerId, startDate_, endDate_);
        List<JobApplicationCountDTO> leastAppliedJobs = new ArrayList<>();
        for (Object[] jobData : leastAppliedJobsData) {
            leastAppliedJobs.add(new JobApplicationCountDTO(
                    (Long) jobData[0],
                    (String) jobData[1],
                    (Long) jobData[2]
            ));
        }
        summary.setLeastAppliedJobs(leastAppliedJobs);

        return summary;
    }


}
