package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.JobService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceIMPL implements JobService {

    Integer pageSize = 10;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private EmployerRepo employerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationRepo applicationRepo;


    @Override
    public String addJob(JobRequestDTO jobRequestDTO) {
        Job job = modelMapper.map(jobRequestDTO, Job.class);
        job.setEmployer(employerRepo.getReferenceById(jobRequestDTO.getEmployer()));
        jobRepo.save(job);
        return jobRequestDTO.getJobTitle()+" is saved. ";
    }
    @Override
    public String deleteJob(Long jobId){
        jobRepo.deleteById(jobId);
        return "Job deleted";
    }
    @Override
    public String updateJob(JobDTO jobDTO) {
        if(jobRepo.existsByJobId(jobDTO.getJobId())) {
            jobRepo.save(modelMapper.map(jobDTO, Job.class));
            return jobDTO.getJobTitle()+" is updated...";
        }else {
            return "Not Found that job...";//ExeptionHandle
        }
    }
    @Override
    public JobDTO viewJobDetails(Long jobId){
        Job job = jobRepo.getJobByJobId(jobId);
        return modelMapper.map(job, JobDTO.class);
    }
    @Override
    public PaginatedResponseJobDTO filterJobByCategory(JobCategory jobCategory, Integer page){
        Page<Job> jobList = jobRepo.findAllByJobCategory(jobCategory, PageRequest.of(page, pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList, new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobCategory(jobCategory)
        );
    }
    @Override
    public PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContaining(location, PageRequest.of(page, pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobLocationsContaining(location)
        );
    }
    @Override
    public PaginatedResponseJobDTO studentJobs(Long studentId, Integer page) {
        Student student = studentRepo.getStudentByUserId(studentId);
        Location location = student.getLocation();
        List<JobCategory> preferences = student.getPreferences();
        Page<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(location,preferences,PageRequest.of(page,pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobLocationsContainingAndJobCategoryIn(location,preferences)
        );
    }
    @Override
    public PaginatedResponseJobDTO SearchJobsByLocationAndCategories(Location location, List<JobCategory> categoryList, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(location,categoryList,PageRequest.of(page, pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobLocationsContainingAndJobCategoryIn(location,categoryList)
        );
    }
    @Override
    public PaginatedResponseJobDTO employerJobs(Long employerId, Integer page) {
        Employer employer = employerRepo.getEmployerByUserId(employerId);
        Page<Job> jobList = jobRepo.findAllByEmployer(employer, PageRequest.of(page,pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByEmployer(employer)
        );
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
