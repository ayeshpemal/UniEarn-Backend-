package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.request.AddJobRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateJobRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.exception.InvalidParametersException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.specification.JobSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    public String addJob(AddJobRequestDTO addJobRequestDTO) {
        Job job = modelMapper.map(addJobRequestDTO, Job.class);
        job.setEmployer(employerRepo.getReferenceById(addJobRequestDTO.getEmployer()));
        jobRepo.save(job);
        return addJobRequestDTO.getJobTitle() + " is saved. ";
    }

    @Override
    public String deleteJob(Long jobId) {
        if (jobRepo.existsByJobId(jobId)) {
            jobRepo.deleteById(jobId);
            return "Job deleted";
        } else {
            throw new NotFoundException("No Job Found In That ID...!!");
        }
    }

    @Override
    public String updateJob(UpdateJobRequestDTO updateJobRequestDTO) {
        if (jobRepo.existsByJobId(updateJobRequestDTO.getJobId())) {
            Job job = modelMapper.map(updateJobRequestDTO, Job.class);
            job.setEmployer(employerRepo.getReferenceById(updateJobRequestDTO.getEmployer()));
            jobRepo.save(job);
            return updateJobRequestDTO.getJobTitle() + " is updated...";
        } else {
            throw new NotFoundException("No Job Found In That ID...!!");
        }
    }

    @Override
    public JobDTO viewJobDetails(long jobId) {
        Job job = jobRepo.getJobByJobId(jobId);
        if (job != null) {
            return modelMapper.map(job, JobDTO.class);
        } else {
            throw new NotFoundException("No Job Found In That ID...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO filterJobByCategory(JobCategory jobCategory, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobCategory(jobCategory, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobCategory(jobCategory)
            );
        } else {
            throw new NotFoundException("No Jobs Found...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContaining(location, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobLocationsContaining(location)
            );
        } else {
            throw new NotFoundException("No Jobs Found...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO searchJobByKeyword(String keyWord, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobDescriptionContaining(keyWord, PageRequest.of(page, pageSize));
        if (jobList.getSize() > 0) {
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.countAllByJobDescriptionContaining(keyWord)
            );
        } else {
            throw new NotFoundException("No Jobs Founds...!!");
        }
    }

    @Override
    public PaginatedResponseJobDTO studentJobs(Long studentId, Integer page) {
        Student student = studentRepo.getStudentByUserId(studentId);
        if (student != null) {
            Location location = student.getLocation();
            List<JobCategory> preferences = student.getPreferences();
            Page<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(location, preferences, PageRequest.of(page, pageSize));
            if (jobList.getSize() > 0) {
                List<JobDTO> jobDTOs = jobList.getContent().stream()
                        .map(job -> modelMapper.map(job, JobDTO.class))
                        .collect(Collectors.toList());
                return new PaginatedResponseJobDTO(
                        jobDTOs,
                        jobRepo.countAllByJobLocationsContainingAndJobCategoryIn(location, preferences)
                );
            } else {
                throw new NotFoundException("No Jobs Found...!!");
            }
        } else {
            throw new NotFoundException("No Student Found In That ID...!!");
        }
    }


    @Override
    public List<JobDetailsResponseDTO> getJobsByUser(long userId, Integer page) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<JobDetailsResponseDTO> jobDetailsResponseDTOS = new ArrayList<>();

        if (user instanceof Student student) {
            // Fetch applied jobs for students
            Page<Application> applicationList = applicationRepo.getAllByStudent(student, PageRequest.of(page, pageSize));

            jobDetailsResponseDTOS = applicationList.getContent().stream()
                    .map(application -> {
                                Job job = jobRepo.getJobByJobId(application.getJob().getJobId());
                                return new JobDetailsResponseDTO(
                                        job.getJobId(),
                                        job.getJobTitle(),
                                        job.getJobCategory().toString(),
                                        job.getJobLocations().toString(),
                                        job.getStartDate(),
                                        job.getEndDate(),
                                        job.isActiveStatus(),
                                        job.getJobPayment(),
                                        application.getStatus().toString()
                                );
                            }
                    )
                    .collect(Collectors.toList());
        }

        if (user instanceof Employer employer) {
            // Fetch posted jobs for employers
            Page<Job> jobs = jobRepo.findAllByEmployer(employer, PageRequest.of(page, pageSize));

            jobDetailsResponseDTOS = jobs.stream()
                    .map(job -> new JobDetailsResponseDTO(
                            job.getJobId(),
                            job.getJobTitle(),
                            job.getJobCategory().toString(),
                            job.getJobLocations().toString(),
                            job.getStartDate(),
                            job.getEndDate(),
                            job.isActiveStatus(),
                            job.getJobPayment(),
                            null // Status is not relevant for posted jobs
                    ))
                    .collect(Collectors.toList());
        }
        return jobDetailsResponseDTOS;
    }



    @Override
    public PaginatedResponseJobDTO searchJobs(Location location, List<JobCategory> categories, String keyword, Integer page) {
        Specification<Job> spec = JobSpecification.filterJobs(location, categories, keyword);
        if(spec==null)
            throw new InvalidParametersException("Invalid Parameters...!!");

        Page<Job> jobList = jobRepo.findAll(spec,PageRequest.of(page, pageSize));
        if(jobList.getSize()>0){
            List<JobDTO> jobDTOs = jobList.getContent().stream()
                    .map(job -> modelMapper.map(job, JobDTO.class))
                    .collect(Collectors.toList());
            return new PaginatedResponseJobDTO(
                    jobDTOs,
                    jobRepo.count(spec)
            );
        }
        else {
            throw new NotFoundException("No Jobs Founds...!!");
        }
    }

    public String setStatus(Long jobId, boolean status){
        if (!jobRepo.existsById(jobId)) {
            throw new NotFoundException("No Job Found with ID: " + jobId);
        }
        try {
            jobRepo.setActiveState(jobId, status);
            return "Set status: "+status;
        }catch (RuntimeException e){
            throw new RuntimeException("Task failed...!!");
        }

    }
}
