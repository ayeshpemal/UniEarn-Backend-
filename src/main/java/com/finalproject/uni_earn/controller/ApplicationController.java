package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.impl.ApplicationServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/application/")
public class ApplicationController {
    @Autowired
    ApplicationServiceIMPL applicationService;

    @PostMapping("/addApplication")
    public ApplicationDTO addApplication(@RequestBody ApplicationDTO applicationDTO) {
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiii");
        return applicationService.addApplication(applicationDTO);
    }
    @PutMapping("/updateStatus/{applicationId}")
    public ApplicationDTO updateStatus(@PathVariable Long applicationId, @RequestBody ApplicationStatus status) {
        // Call the service to update the application status
        return applicationService.updateStatus(applicationId, status);
    }

    @GetMapping("/viewApplicationDetails/{applicationId}")
    public ApplicationDTO viewApplicationDetails(@PathVariable Long applicationId) {
        // Call the service to fetch application details
        return applicationService.viewApplicationDetails(applicationId);
    }

    @DeleteMapping("/deleteApplication/{applicationId}")
    public String deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return "Application with ID " + applicationId + " has been deleted.";
    }

    @PutMapping("/updateApplication/{applicationId}")
    public ApplicationDTO updateApplication(@PathVariable Long applicationId, @RequestBody ApplicationDTO applicationDTO) {
        return applicationService.updateApplication(applicationId, applicationDTO);
    }
}

