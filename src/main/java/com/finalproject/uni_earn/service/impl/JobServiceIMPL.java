package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.JobService;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional
public class JobServiceIMPL implements JobService {
    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private EmployerRepo employerRepo;


    @Override
    public String addJob(JobRequestDTO jobRequestDTO) {
        Job job = modelMapper.map(jobRequestDTO, Job.class);
        job.setEmployer(employerRepo.getReferenceById(jobRequestDTO.getEmployer()));
        jobRepo.save(job);
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

    @Override
    public List<JobDetailsResponseDTO> getJobsByUser(Long userId) {
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<JobDetailsResponseDTO> jobDetailsResponseDTOS = new ArrayList<>();

        if (user instanceof Student student){
            // Fetch applied jobs for students
            List<Application> applications = applicationRepo.getAllByStudent(student);

            jobDetailsResponseDTOS = applications.stream()
                    .map(application -> {
                        Job job = jobRepo.getJobByJobId(application.getJob().getJobId());
                        return new JobDetailsResponseDTO(
                                job.getJobId(),
                                job.getJobTitle(),
                                job.getJobCategory().toString(),
                                job.getJobLocation().toString(),
                                job.getStartDate(),
                                job.getEndDate(),
                                job.isJobStatus(),
                                job.getJobPayment(),
                                application.getStatus().toString()
                            );
                        }
                    )
                    .collect(Collectors.toList());
        }

        if (user instanceof Employer employer) {
            // Fetch posted jobs for employers
            List<Job> jobs = jobRepo.findAllByEmployer(employer);

            jobDetailsResponseDTOS = jobs.stream()
                    .map(job -> new JobDetailsResponseDTO(
                            job.getJobId(),
                            job.getJobTitle(),
                            job.getJobCategory().toString(),
                            job.getJobLocation().toString(),
                            job.getStartDate(),
                            job.getEndDate(),
                            job.isJobStatus(),
                            job.getJobPayment(),
                            null // Status is not relevant for posted jobs
                    ))
                    .collect(Collectors.toList());
        }
        return jobDetailsResponseDTOS;
    }

    //List<Job> findAllByJobLocationAndCategory(Location location, JobCategory jobCategory){};

}
