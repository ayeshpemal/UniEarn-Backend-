package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.TeamRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.UpdateNotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
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
    private TeamRepo teamRepository;

    // Inject notification service
    @Autowired
    private UpdateNotificationService updateNotificationService;

    @Override
    public String applyAsStudent(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user: Only students can apply for jobs"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job not found"));

        if(job.getRequiredWorkers() != 1) {
            throw new NotAcceptableStatusException("This job requires a team, please apply as a team.");
        }

        if (applicationRepository.existsByJob_JobIdAndStudent_UserId(jobId, studentId)) {
            throw new RuntimeException("You have already applied for this job.");
        }

        Application application = new Application();
        application.setJob(job);
        application.setStudent(student);
        application.setAppliedDate(new Date());
        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);
        return "Student application submitted successfully!";
    }

    @Override
    public String applyAsTeam(Long teamId, Long jobId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (applicationRepository.existsByJob_JobIdAndTeam_Id(jobId, teamId)) {
            throw new AlreadyExistException("This team has already applied for this job.");
        }

        if (teamRepository.findMembersCountByTeamId(teamId) != job.getRequiredWorkers()) {
            throw new RuntimeException("Team size does not match required workers.");
        }


        Application application = new Application();
        application.setJob(job);
        application.setTeam(team);
        application.setAppliedDate(new Date());
        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);
        return "Team application submitted successfully!";
    }

    @Override
    public String updateStatus(Long applicationId, ApplicationStatus status) {

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

        return "Application status updated to " + status;
    }

    @Override
    public ApplicationDTO viewApplicationDetails(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setApplicationId(application.getApplicationId());
            applicationDTO.setJobId(application.getJob().getJobId());
            if(application.getStudent() != null){
                applicationDTO.setUserId(application.getStudent().getUserId()); // Changed from getUserId() to getStudentId()
            }
            if (application.getTeam() != null) {
                applicationDTO.setTeamId(application.getTeam().getId());
            }
            applicationDTO.setStatus(application.getStatus().name());
            applicationDTO.setAppliedDate(application.getAppliedDate());

            return applicationDTO;
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    @Override
    public void deleteApplication(Long applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if (applicationOptional.isPresent()) {
            applicationRepository.deleteById(applicationId);
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    @Override
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
