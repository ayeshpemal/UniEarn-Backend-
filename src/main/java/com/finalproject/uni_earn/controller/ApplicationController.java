package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.impl.ApplicationServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/application/")
public class ApplicationController {
    @Autowired
    ApplicationServiceIMPL applicationService;
    @Autowired
    UserRepo userRepository;

    //@PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/apply/student")
    public ResponseEntity<StandardResponse> applyAsStudent(@RequestParam Long studentId, @RequestParam Long jobId) {
        String message = applicationService.applyAsStudent(studentId,jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "success", message),
                HttpStatus.CREATED);
    }

    //@PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/apply/team")
    public ResponseEntity<StandardResponse> applyAsTeam(@RequestParam Long teamId, @RequestParam Long jobId) {
        String message = applicationService.applyAsTeam(teamId, jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success", message),
                HttpStatus.CREATED);
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus newStatus,
            @RequestHeader("userId") Long userId) {

        // Assuming you have a method to fetch the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        try {
            applicationService.updateStatus(applicationId, newStatus, user);
            return ResponseEntity.ok("Application status updated successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/viewApplicationDetails/{applicationId}")
    public ResponseEntity<StandardResponse> viewApplicationDetails(@PathVariable Long applicationId) {
        // Call the service to fetch application details
        ApplicationDTO applicationDTO = applicationService.viewApplicationDetails(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", applicationDTO),
                HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @DeleteMapping("/deleteApplication/{applicationId}")
    public ResponseEntity<StandardResponse> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", "Application deleted successfully"),
                HttpStatus.OK);
    }

    @GetMapping("/student/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getStudentApplicationsSummary(@PathVariable Long userId) {

        Map<String, Object> summary = applicationService.getStudentApplicationsSummary(userId);


        return ResponseEntity.ok(summary);
    }
}

