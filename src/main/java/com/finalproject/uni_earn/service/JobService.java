package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.AddJobRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateJobRequestDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;

import java.util.Date;
import java.util.List;

public interface JobService {

    //For Employer
    String addJob(AddJobRequestDTO addJobRequestDTO);
    String deleteJob(Long jobId);
    String updateJob(UpdateJobRequestDTO updateJobRequestDTO);
    String setStatus(Long jobId, boolean status);

    //For Student
    PaginatedResponseJobDTO studentJobs(Long studentId, Integer page);
    PaginatedResponseJobDTO filterJobByCategory(JobCategory jobCategory, Integer page);
    PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page);
    PaginatedResponseJobDTO searchJobByKeyword(String keyWord, Integer page);
    PaginatedResponseJobDTO searchJobs(Location location, List<JobCategory> categories, String keyword, Date startDate, Integer page);

    //Common
    JobDTO viewJobDetails(long jobId);
    PaginatedJobDetailsResponseDTO getJobsByUser(long userId, Integer page);

}
