package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.JobDTO;

import java.util.List;

public interface JobService {

    JobDTO updateJobDetails(JobDTO jobDTO);
    String deleteJob(int jobId);
    JobDTO viewJobDetails(int jobId);
    List<JobDTO> filterJob(String category);
    JobDTO addJob(JobDTO jobDTO);

}
