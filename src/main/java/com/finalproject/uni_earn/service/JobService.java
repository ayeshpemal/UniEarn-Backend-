package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.dto.request.SearchJobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;

import java.util.List;

public interface JobService {

    //For Employer
    AddJobResponce addJob(JobRequestDTO jobRequestDTO);
    String deleteJob(Long jobId);
    String updateJobDetails(JobDTO jobDTO);
    List<JobDTO> employerJobs(Long employerId);

    //For Student
    List<JobDTO> studentJobs(Long studentId);
    List<JobDTO> filterJob(JobCategory jobCategory);
    List<JobDTO> findAllByJobLocationsContaining(Location location);
    List<JobDTO> findAllByJobLocationsContainingAndJobCategoryIn(SearchJobRequestDTO searchJobRequestDto);

    //Common
    JobDTO viewJobDetails(Long jobId);

}
