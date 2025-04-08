package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.dto.request.AdminStatsRequestDTO;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import com.finalproject.uni_earn.service.AdminService;
import com.finalproject.uni_earn.service.UserService;
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
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/make-admin/{userId}")
    public ResponseEntity<StandardResponse> makeAdmin(@PathVariable Long userId) {
        String message = adminService.makeUserAdmin(userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Admin created successfully.", message),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove-admin/{userId}")
    public ResponseEntity<StandardResponse> removeAdmin(@PathVariable Long userId) {
        String message = adminService.removeAdmin(userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Admin removed successfully.", message),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-admins")
    public ResponseEntity<StandardResponse> getAllAdmins() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Admins fetched successfully.", adminService.getAllAdmins()),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/stats")
    @Operation(summary = "Get platform statistics", description = "Retrieves platform statistics within a given date range.")
    public ResponseEntity<StandardResponse> getPlatformStatistics(@RequestBody AdminStatsRequestDTO request) {
        AdminStatsResponseDTO response = adminService.getPlatformStatistics(request.getStartDate(), request.getEndDate());
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("notification/broadcast")
    public ResponseEntity<StandardResponse> broadcastNotification(@RequestBody String message) {
        String response = adminService.broadcastNotification(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification broadcast successfully.", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("notification/send-to-user/{userId}")
    public ResponseEntity<StandardResponse> sendNotificationToUser(@PathVariable Long userId, @RequestBody String message) {
        String response = adminService.sendNotificationToUser(userId, message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent successfully.", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("notification/send-to-all-employers")
    public ResponseEntity<StandardResponse> sendNotificationAllEmployers(@RequestBody String message) {
        String response = adminService.sendNotificationAllEmployers(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent to all employers successfully.", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("notification/send-to-all-students")
    public ResponseEntity<StandardResponse> sendNotificationAllStudents(@RequestBody String message) {
        String response = adminService.sendNotificationAllStudents(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent to all students successfully.", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("notification/send-to-all-admins")
    public ResponseEntity<StandardResponse> sendNotificationAllAdmins(@RequestBody String message) {
        String response = adminService.sendNotificationAllAdmins(message);
        return new ResponseEntity<>(
                new StandardResponse(200, "Notification sent to all admins successfully.", response),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("notification/private")
    public ResponseEntity<StandardResponse> getPrivateAdminNotifications(
            @RequestParam(required = false) Long userID,
            @RequestParam NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Private notifications fetched successfully.", adminService.getPrivateAdminNotifications(userID, type, page, size)),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate-dummy-users")
    public ResponseEntity<StandardResponse> generateDummyUsers() {
        adminService.generateDummyUsers(userService);
        return new ResponseEntity<>(
                new StandardResponse(200, "Dummy users generated successfully.", null),
                HttpStatus.OK
        );
    }
}
