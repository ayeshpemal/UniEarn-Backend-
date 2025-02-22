package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.service.EmployerService;
import com.finalproject.uni_earn.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;


    //@PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/applications/select/{applicationId}")
    public ResponseEntity<String> selectCandidate(@PathVariable int applicationId) {
        String response = employerService.selectCandidate(applicationId); // Call service method
        return ResponseEntity.ok(response); // Return the result
    }

}
