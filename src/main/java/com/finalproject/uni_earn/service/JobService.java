package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;

import java.util.List;

public interface JobService {

    //For Employer
    AddJobResponce addJob(JobRequestDTO jobRequestDTO);
    String deleteJob(Long jobId);
    String updateJob(JobDTO jobDTO);
    PaginatedResponseJobDTO employerJobs(Long employerId, Integer page);

    //For Student
    PaginatedResponseJobDTO studentJobs(Long studentId, Integer page);
    PaginatedResponseJobDTO filterJobByCategory(JobCategory jobCategory, Integer page);
    PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page);
    PaginatedResponseJobDTO SearchJobsByLocationAndCategories(Location location, List<JobCategory> categoryList, Integer page);

    //Common
    JobDTO viewJobDetails(Long jobId);

    JobDTO viewJobDetails(int jobId);
    List<JobDTO> filterJob(JobCategory jobCategory);
    List<JobDTO> findAllByEmployer(Employer employer);
    List<JobDTO> findAllByJobLocation(Location location);

    List<JobDetailsResponseDTO> getJobsByUser(Long userId);

}
