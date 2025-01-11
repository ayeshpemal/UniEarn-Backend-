package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.service.JobService;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
public class JobServiceIMPL implements JobService {
    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobDTO updateJobDetails(JobDTO jobDTO) {
        jobRepo.save(modelMapper.map(jobDTO, Job.class));
        return jobDTO;
    }
    @Override
    public String deleteJob(int jobId){
        jobRepo.deleteById(jobId);
        return "Job deleted";
    }
    @Override
    public JobDTO viewJobDetails(int jobId){
        Job job = jobRepo.getJobByById(jobId);
        return modelMapper.map(job, JobDTO.class);
    }
    @Override
    public List<JobDTO> filterJob(String category){
        List<Job> jobList = jobRepo.filterjob(category);
        return modelMapper.map(jobList, new TypeToken<List<JobDTO>>(){}.getType());
    }
    @Override
    public JobDTO addJob(JobDTO jobDTO) {
        jobRepo.save(modelMapper.map(jobDTO, Job.class));
        return jobDTO;
    }

}
