package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.service.UpdateNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/application/")
public class UpdateNotifiController {

    private final UpdateNotificationService updateNotificationService;


    public UpdateNotifiController(UpdateNotificationService updateNotificationService) {
        this.updateNotificationService = updateNotificationService;
    }

    @PostMapping("/createtoEmployee/{applicationId}")
    public ResponseEntity<String> createNotification(@PathVariable Long applicationId) {
        try {
            updateNotificationService.createNotification(applicationId);
            return ResponseEntity.ok("Notification created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
