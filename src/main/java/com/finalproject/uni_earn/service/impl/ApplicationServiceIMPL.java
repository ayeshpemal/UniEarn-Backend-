package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.*;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.InvalidValueException;
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

import java.util.*;
import java.util.stream.Collectors;

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

        if (job.getRequiredWorkers() != 1) {
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

        updateNotificationService.createNotification(application.getApplicationId());
        return "Student application submitted successfully!";
    }

    @Override
    public String applyAsTeam(Long teamId, Long jobId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        team.getMembers().forEach(member -> {
            if (applicationRepository.isStudentInAppliedTeam(jobId,member.getUserId())) {
                throw new InvalidValueException("One or more team members have already applied for this job.");
            }
        });

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
        updateNotificationService.createNotification(application.getApplicationId());
        return "Team application submitted successfully!";
    }

    @Override
    public void updateStatus(Long applicationId, ApplicationStatus newStatus, User user) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        if (user.getRole() == Role.EMPLOYER) {
            if (newStatus == ApplicationStatus.ACCEPTED || newStatus == ApplicationStatus.REJECTED) {
                application.setStatus(newStatus);
            } else {
                throw new InvalidValueException("Employer can only accept or reject applications.");
            }
        } else if (user.getRole() == Role.STUDENT) {
            if (application.getStatus() == ApplicationStatus.ACCEPTED && newStatus == ApplicationStatus.CONFIRMED) {
                application.setStatus(newStatus);
            } else {
                throw new InvalidValueException("Student can only confirm an accepted application.");
            }
        } else {
            throw new InvalidValueException("Invalid user role.");
        }

        applicationRepository.save(application);
        updateNotificationService.createNotification(applicationId);
    }

    @Override
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
    public Map<String, Object> getStudentApplicationsSummary(Long userId) {
        
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + userId));


        List<Application> individualapplications = applicationRepository.findByStudent(student);
        List<Application> teamApplications = applicationRepository.findApplicationsByStudentInTeam(userId, null).getContent();
        
        List<Application> applications = new ArrayList<>();
        applications.addAll(individualapplications);
        applications.addAll(teamApplications);

        Map<ApplicationStatus, Long> statusCounts = applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));


        long totalApplications = applications.size();


        Map<String, Double> statusPercentages = new HashMap<>();
        statusCounts.forEach((status, count) -> {
            double percentage = (count * 100.0) / totalApplications;
            statusPercentages.put(status.name(), percentage);
        });


        Map<JobCategory, Long> categoryBreakdown = applications.stream()
                .collect(Collectors.groupingBy(
                        application -> application.getJob().getJobCategory(), // Group by JobCategory enum
                        Collectors.counting() // Count the number of applications in each category
                ));


        Map<String, Object> response = new HashMap<>();
        response.put("totalApplications", totalApplications);
        response.put("pending", statusCounts.getOrDefault(ApplicationStatus.PENDING, 0L));
        response.put("accepted", statusCounts.getOrDefault(ApplicationStatus.ACCEPTED, 0L));
        response.put("rejected", statusCounts.getOrDefault(ApplicationStatus.REJECTED, 0L));
        response.put("confirmed", statusCounts.getOrDefault(ApplicationStatus.CONFIRMED, 0L)); // Added CONFIRMED status
        response.put("statusPercentages", statusPercentages);
        response.put("categoryBreakdown", categoryBreakdown);

        return response;
    }

    public boolean hasStudentAppliedForJob(Long studentId, Long jobId) {
        if(!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found with ID: " + studentId);
        }
        if(!jobRepository.existsById(jobId)) {
            throw new NotFoundException("Job not found with ID: " + jobId);
        }
        // Check if student applied individually
        if (applicationRepository.existsByJob_JobIdAndStudent_UserId(jobId, studentId)) {
            return true;
        }

        // Check if student is in a team that applied
        return applicationRepository.isStudentInAppliedTeam(jobId, studentId);
    }
}

