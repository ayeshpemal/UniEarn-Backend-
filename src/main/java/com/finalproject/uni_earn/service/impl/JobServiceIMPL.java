package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
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
    public String addJob(JobRequestDTO jobRequestDTO) {
        jobRepo.save(modelMapper.map(jobRequestDTO, Job.class));
        return jobRequestDTO.getJobTitle()+" is saved. ";
    }
    @Override
    public String deleteJob(int jobId){
        jobRepo.deleteById(jobId);
        return "Job deleted";
    }
    @Override
    public JobDTO updateJobDetails(JobDTO jobDTO) {
        jobRepo.save(modelMapper.map(jobDTO, Job.class));
        return jobDTO;
    }
    @Override
    public JobDTO viewJobDetails(int jobId){
        Job job = jobRepo.getJobByJobId(jobId);
        return modelMapper.map(job, JobDTO.class);
    }
    @Override
    public List<JobDTO> filterJob(JobCategory jobCategory){
        List<Job> jobList = jobRepo.findAllByJobCategory(jobCategory);
        return modelMapper.map(jobList, new TypeToken<List<JobDTO>>(){}.getType());
    }

    @Override
    public List<JobDTO> findAllByEmployer(Employer employer) {
        List<Job> list = jobRepo.findAllByEmployer(employer);
        return modelMapper.map(list,new TypeToken<List<JobDTO>>(){}.getType());
    }

    @Override
    public List<JobDTO> findAllByJobLocation(Location location) {
        List<Job> list = jobRepo.findAllByJobLocation(location);
        return modelMapper.map(list,new TypeToken<List<JobDTO>>(){}.getType());
    }

    //List<Job> findAllByJobLocationAndCategory(Location location, JobCategory jobCategory){};

}
