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
import com.finalproject.uni_earn.service.JobService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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


    @Override
    public AddJobResponce addJob(JobRequestDTO jobRequestDTO) {
        jobRepo.save(modelMapper.map(jobRequestDTO, Job.class));
        String message = jobRequestDTO.getJobTitle()+" is saved. ";
        List<Student> studentList = studentRepo.findAllByPreferencesContaining(jobRequestDTO.getJobCategory());
        AddJobResponce responce = new AddJobResponce(message,studentList);
        return responce;
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

    public PaginatedResponseJobDTO SearchJobByLocation(Location location, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContaining(location, PageRequest.of(page, pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobLocationsContaining(location)
        );
    }

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

    public PaginatedResponseJobDTO SearchJobsByLocationAndCategories(Location location, List<JobCategory> categoryList, Integer page) {
        Page<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(location,categoryList,PageRequest.of(page, pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByJobLocationsContainingAndJobCategoryIn(location,categoryList)
        );
    }

    public PaginatedResponseJobDTO employerJobs(Long employerId, Integer page) {
        Employer employer = employerRepo.getEmployerByUserId(employerId);
        Page<Job> jobList = jobRepo.findAllByEmployer(employer, PageRequest.of(page,pageSize));
        return new PaginatedResponseJobDTO(
                modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType()),
                jobRepo.countAllByEmployer(employer)
        );
    }

}
