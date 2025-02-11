package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Transactional
@Service
public class ApplicationServiceIMPL implements ApplicationService {

    @Autowired
    private ApplicationRepo applicationRepository;

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private UpdateNotificationService updateNotificationService; // Inject notification service



    public ApplicationDTO addApplication(ApplicationDTO applicationDTO) {

        Job job = jobRepository.findById(applicationDTO.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job not found"));

        Optional<Student> studentOpt = studentRepository.findById(applicationDTO.getUserId());
        if (studentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user: Only students can apply for jobs");
        }
        Student student = studentOpt.get();

        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(applicationDTO.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + applicationDTO.getStatus());
        }

        Date appliedDate = (applicationDTO.getAppliedDate() != null)
                ? applicationDTO.getAppliedDate()
                : new Date();

        Application application = new Application();
        application.setJob(job);
        application.setStudent(student);
        application.setStatus(status);
        application.setAppliedDate(appliedDate);

        Application savedApplication = applicationRepository.save(application);

        // Call notification service to notify the employer
        updateNotificationService.createNotification(savedApplication.getApplicationId());

        return applicationDTO;
    }

    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatus status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = application.getJob();

        User employer = job.getEmployer();

        if (!"EMPLOYER".equalsIgnoreCase(String.valueOf(employer.getRole()))) {
            throw new RuntimeException("Only employers can change the application status.");
        }

        application.setStatus(status);
        applicationRepository.save(application);

        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setApplicationId(application.getApplicationId());
        applicationDTO.setJobId(application.getJob().getJobId());
        applicationDTO.setUserId(application.getStudent().getUserId()); // Changed from getUserId() to getStudentId()
        applicationDTO.setStatus(application.getStatus().name());
        applicationDTO.setAppliedDate(application.getAppliedDate());

        return applicationDTO;
    }

    public ApplicationDTO viewApplicationDetails(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setApplicationId(application.getApplicationId());
            applicationDTO.setJobId(application.getJob().getJobId());
            applicationDTO.setUserId(application.getStudent().getUserId()); // Changed from getUserId() to getStudentId()
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
            applicationRepository.deleteById(applicationId);
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    public ApplicationDTO updateApplication(Long applicationId, ApplicationDTO applicationDTO) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Student student = application.getStudent();


        application.setStatus(ApplicationStatus.valueOf(applicationDTO.getStatus()));
        application.setAppliedDate(applicationDTO.getAppliedDate());

        applicationRepository.save(application);

        applicationDTO.setApplicationId(application.getApplicationId());
        applicationDTO.setJobId(application.getJob().getJobId());
        applicationDTO.setUserId(application.getStudent().getUserId()); // Changed from getUserId() to getStudentId()
        applicationDTO.setStatus(application.getStatus().name());
        applicationDTO.setAppliedDate(application.getAppliedDate());

        return applicationDTO;
    }
}
