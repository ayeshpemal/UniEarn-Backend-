package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.impl.ApplicationServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/application/")
public class ApplicationController {
    @Autowired
    ApplicationServiceIMPL applicationService;

    @PostMapping("/apply/student")
    public ResponseEntity<StandardResponse> addApplication(@RequestParam Long studentId, @RequestParam Long jobId) {
        String message = applicationService.applyAsStudent(studentId, jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "success", message),
                HttpStatus.CREATED);
    }

    @PostMapping("/apply/team")
    public ResponseEntity<StandardResponse> applyAsTeam(@RequestParam Long teamId, @RequestParam Long jobId) {
        String message = applicationService.applyAsTeam(teamId, jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success", message),
                HttpStatus.CREATED);
    }

    @PutMapping("/updateStatus/{applicationId}")
    public ResponseEntity<StandardResponse> updateStatus(@PathVariable Long applicationId, @RequestBody ApplicationStatus status) {
        // Call the service to update the application status
        String message = applicationService.updateStatus(applicationId, status);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @GetMapping("/viewApplicationDetails/{applicationId}")
    public ResponseEntity<StandardResponse> viewApplicationDetails(@PathVariable Long applicationId) {
        // Call the service to fetch application details
        ApplicationDTO applicationDTO = applicationService.viewApplicationDetails(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", applicationDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/deleteApplication/{applicationId}")
    public ResponseEntity<StandardResponse> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", "Application deleted successfully"),
                HttpStatus.OK);
    }
}