package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.dto.request.AdminStatsRequestDTO;
import com.finalproject.uni_earn.service.AdminService;
import com.finalproject.uni_earn.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value="/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/make-admin/{userId}")
    public ResponseEntity<StandardResponse> makeAdmin(@PathVariable Long userId) {
        String message = adminService.makeUserAdmin(userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Admin created successfully.", message),
                HttpStatus.OK
        );
    }

    @PostMapping("/remove-admin/{userId}")
    public ResponseEntity<StandardResponse> removeAdmin(@PathVariable Long userId) {
        String message = adminService.removeAdmin(userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Admin removed successfully.", message),
                HttpStatus.OK
        );
    }

    @GetMapping("/all-admins")
    public ResponseEntity<StandardResponse> getAllAdmins() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Admins fetched successfully.", adminService.getAllAdmins()),
                HttpStatus.OK
        );
    }

    @PostMapping("/stats")
    @Operation(summary = "Get platform statistics", description = "Retrieves platform statistics within a given date range.")
    public ResponseEntity<StandardResponse> getPlatformStatistics(@RequestBody AdminStatsRequestDTO request) {
        AdminStatsResponseDTO response = adminService.getPlatformStatistics(request.getStartDate(), request.getEndDate());
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", response),
                HttpStatus.OK
        );
    }

    @PostMapping("notification/broadcast")
    public ResponseEntity<StandardResponse> broadcastNotification(@RequestBody String message) {
        String response = adminService.broadcastNotification(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification broadcast successfully.", response),
                HttpStatus.OK
        );
    }

    @PostMapping("notification/send-to-user/{userId}")
    public ResponseEntity<StandardResponse> sendNotificationToUser(@PathVariable Long userId, @RequestBody String message) {
        String response = adminService.sendNotificationToUser(userId, message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent successfully.", response),
                HttpStatus.OK
        );
    }

    @PostMapping("notification/send-to-all-employers")
    public ResponseEntity<StandardResponse> sendNotificationAllEmployers(@RequestBody String message) {
        String response = adminService.sendNotificationAllEmployers(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent to all employers successfully.", response),
                HttpStatus.OK
        );
    }

    @PostMapping("notification/send-to-all-students")
    public ResponseEntity<StandardResponse> sendNotificationAllStudents(@RequestBody String message) {
        String response = adminService.sendNotificationAllStudents(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent to all students successfully.", response),
                HttpStatus.OK
        );
    }
}
