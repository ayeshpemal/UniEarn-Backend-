package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.service.UpdateNotificationService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/updateNotification/")
public class UpdateNotifiController {

    private final UpdateNotificationService updateNotificationService;


    public UpdateNotifiController(UpdateNotificationService updateNotificationService) {
        this.updateNotificationService = updateNotificationService;
    }

    //@PreAuthorize("hasRole('EMPLOYER') or hasRole('STUDENT')")
    @PostMapping("/create-update-notification/{applicationId}")
    public ResponseEntity<StandardResponse> createNotification(@PathVariable Long applicationId) {
        try {
            updateNotificationService.createNotification(applicationId);
            return ResponseEntity.ok(new StandardResponse(201, "Notification created successfully.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StandardResponse(400, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new StandardResponse(500, "Internal Server Error", null));
        }
    }

    //@PreAuthorize("hasRole('EMPLOYER') or hasRole('STUDENT')")
    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<StandardResponse> markNotificationAsRead(@PathVariable Long notificationId) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", updateNotificationService.markNotificationAsRead(notificationId)),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    @GetMapping("/get-update-notifications/{userId}")
    public ResponseEntity<StandardResponse> getPaginatedUpdateNotifications(@PathVariable Long userId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", updateNotificationService.getPaginatedUpdateNotification(userId, page, size)),
                HttpStatus.OK
        );
    }
}
