package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.dto.request.SearchJobRequestDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
public class JobServiceIMPL implements JobService {
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
    public String updateJobDetails(JobDTO jobDTO) {
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
    public List<JobDTO> filterJob(JobCategory jobCategory){
        List<Job> jobList = jobRepo.findAllByJobCategory(jobCategory);
        return modelMapper.map(jobList, new TypeToken<List<JobDTO>>(){}.getType());
    }

    public List<JobDTO> findAllByJobLocationsContaining(Location location) {
        List<Job> jobList = jobRepo.findAllByJobLocationsContaining(location);
        return modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType());
    }

    public List<JobDTO> studentJobs(Long studentId) {
        Student student = studentRepo.getStudentByUserId(studentId);
        List<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(student.getLocation(),student.getPreferences());
        return modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType());
    }

    public List<JobDTO> findAllByJobLocationsContainingAndJobCategoryIn(SearchJobRequestDTO searchJobRequestDto) {
        Location location = searchJobRequestDto.getLocation();
        List<JobCategory> categoryList = searchJobRequestDto.getCategoryList();
        List<Job> jobList = jobRepo.findAllByJobLocationsContainingAndJobCategoryIn(location,categoryList);
        return modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType());
    }

    public List<JobDTO> employerJobs(Long employerId) {
        Employer employer = employerRepo.getEmployerByUserId(employerId);
        List<Job> jobList = jobRepo.findAllByEmployer(employer);
        return modelMapper.map(jobList,new TypeToken<List<JobDTO>>(){}.getType());
    }

}
