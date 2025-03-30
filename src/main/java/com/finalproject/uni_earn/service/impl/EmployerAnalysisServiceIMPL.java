package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedCategoryStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobSummeryDTO;
import com.finalproject.uni_earn.dto.Response.JobCategoryStatisticsDTO;
import com.finalproject.uni_earn.dto.Response.JobStaticsDTO;
import com.finalproject.uni_earn.dto.Response.JobSummeryDetails;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.JobStatus;
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

import java.util.Date;
import java.util.Optional;

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


}
