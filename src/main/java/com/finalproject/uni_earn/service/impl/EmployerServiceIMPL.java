package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.repo.*;
import com.finalproject.uni_earn.service.EmployerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceIMPL implements EmployerService {
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
    public String selectCandidate(long applicationId) {
        // Fetch the application by ID
        Application application = applicationRepo.findById(applicationId).orElse(null);

        if (application == null) {
            return "Application not found!";
        }

        // Update the application status to SELECTED (using the enum)
        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepo.save(application); // Save the updated application

        // Deactivate the job after selecting the candidate
        Job job = application.getJob();
        job.setActiveStatus(false); // Set the job as inactive
        jobRepo.save(job); // Save the updated job

        return "Candidate selected successfully for job: " + job.getJobTitle();
    }




}
