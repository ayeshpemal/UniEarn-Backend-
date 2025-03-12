package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.service.JobNotificationService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/Notification/")
public class JobNotificationController {
    @Autowired
    private JobNotificationService notificationService;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private EmployerRepo employerRepo;



    //@PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<StandardResponse> markNotificationAsRead(@PathVariable Long id) {
        boolean isUpdated = notificationService.markAsRead(id);
        if (isUpdated) {
            return ResponseEntity.ok(new StandardResponse(200, "Notification marked as read.", isUpdated));
        } else {
            return ResponseEntity.badRequest().body(new StandardResponse(400, "Error in notification marked as read.", null));
        }
    }

    //@PreAuthorize("hasRole('STUDENT')")
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

    //@PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/get-job-notifications/{userId}")
    public ResponseEntity<StandardResponse> getPaginatedJobNotifications(@PathVariable Long userId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", notificationService.getPaginatedJobNotification(userId, page, size)),
                HttpStatus.OK
        );
    }
}