package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import com.finalproject.uni_earn.service.impl.NotificationServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/Notification/")
public class NotificationController {
    @Autowired
    private NotificationServiceIMPL notificationService;

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

}