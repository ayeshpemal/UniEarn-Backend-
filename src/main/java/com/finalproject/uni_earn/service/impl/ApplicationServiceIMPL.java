package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;

import com.finalproject.uni_earn.repo.UserRepo;

import com.finalproject.uni_earn.service.ApplicationService;
import jakarta.transaction.Transactional;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Transactional
@Service
public class ApplicationServiceIMPL implements ApplicationService {

    @Autowired
    private ApplicationRepo applicationRepository;

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public ApplicationDTO addApplication(ApplicationDTO applicationDTO) {

        Job job = jobRepository.findById(applicationDTO.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findById(applicationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        Application application = new Application();
        application.setJob(job);
        application.setStudent(user);
        application.setStatus(ApplicationStatus.valueOf(applicationDTO.getStatus()));
        application.setAppliedDate(applicationDTO.getAppliedDate());


        applicationRepository.save(application);


        return applicationDTO;
    }
    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatus status) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            application.setStatus(status);
            applicationRepository.save(application);

            // Convert Application entity to DTO and return it
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setApplicationId(application.getApplicationId());
            applicationDTO.setJobId(application.getJob().getJobId());
            applicationDTO.setUserId(application.getStudent().getUserId());
            applicationDTO.setStatus(application.getStatus().name());
            applicationDTO.setAppliedDate(application.getAppliedDate());

            return applicationDTO;
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    public ApplicationDTO viewApplicationDetails(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            // Convert Application entity to DTO
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setApplicationId(application.getApplicationId());
            applicationDTO.setJobId(application.getJob().getJobId());
            applicationDTO.setUserId(application.getStudent().getUserId());
            applicationDTO.setStatus(application.getStatus().name());
            applicationDTO.setAppliedDate(application.getAppliedDate());

            return applicationDTO;
        } else {
            throw new RuntimeException("Application not found");
        }
    }
    public void deleteApplication(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            // Delete the application
            applicationRepository.deleteById(applicationId);
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    // Function to update an application
    public ApplicationDTO updateApplication(Long applicationId, ApplicationDTO applicationDTO) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Update application fields
        application.setStatus(ApplicationStatus.valueOf(applicationDTO.getStatus()));
        application.setAppliedDate(applicationDTO.getAppliedDate());

        // Save the updated application
        applicationRepository.save(application);

        // Return the updated ApplicationDTO
        applicationDTO.setApplicationId(application.getApplicationId());
        applicationDTO.setJobId(application.getJob().getJobId());
        applicationDTO.setUserId(application.getStudent().getUserId());
        applicationDTO.setStatus(application.getStatus().name());
        applicationDTO.setAppliedDate(application.getAppliedDate());

        return applicationDTO;
    }
}


