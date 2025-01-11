package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;

import java.util.List;

public interface JobService {

    String addJob(JobRequestDTO jobRequestDTO);
    String deleteJob(int jobId);
    JobDTO updateJobDetails(JobDTO jobDTO);

    JobDTO viewJobDetails(int jobId);
    List<JobDTO> filterJob(JobCategory jobCategory);
    List<JobDTO> findAllByEmployer(Employer employer);
    List<JobDTO> findAllByJobLocation(Location location);
    //List<Job> findAllByJobLocationAndCategory(Location location, JobCategory jobCategory);

}
