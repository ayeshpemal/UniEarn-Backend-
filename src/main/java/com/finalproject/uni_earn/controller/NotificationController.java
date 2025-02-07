package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import com.finalproject.uni_earn.service.impl.NotificationServiceIMPL;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/Notification/")
public class NotificationController {
    @Autowired
    private NotificationServiceIMPL notificationService;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private EmployerRepo employerRepo;



    @PostMapping("/createtoEmployee/{applicationId}")
    public ResponseEntity<String> createNotification(@PathVariable Long applicationId) {
        try {
            notificationService.createNotification(applicationId);
            return ResponseEntity.ok("Notification created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long id) {
        boolean isUpdated = notificationService.markAsRead(id);
        if (isUpdated) {
            return ResponseEntity.ok("Notification marked as read.");
        } else {
            return ResponseEntity.badRequest().body("Notification not found or already read.");
        }
    }
    @PostMapping("/create-follow-notification/{jobId}")
    public ResponseEntity<Map<String, String>> createFollowNotification(@PathVariable Long jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));

        Employer employer = job.getEmployer();
        if (employer == null) {
            return ResponseEntity.badRequest().build();
        }

        String notificationMessage = notificationService.createFollowNotification(employer, job);


        Map<String, String> response = new HashMap<>();
        response.put("jobTitle", job.getJobTitle());
        response.put("description", job.getJobDescription());
        response.put("notificationMessage", notificationMessage);

        return ResponseEntity.ok(response);
    }

}