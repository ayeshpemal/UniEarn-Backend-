package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/job/")
public class JobController {

    @Autowired
    JobServiceIMPL jobServiceIMPL;

    @PostMapping("/addjob")
    public String addJob(@RequestBody JobRequestDTO jobRequestDTO) {
        return jobServiceIMPL.addJob(jobRequestDTO);
    }

    @DeleteMapping("/deletejob/{jobId}")
    String deleteJob(@PathVariable int jobId) {
        return jobServiceIMPL.deleteJob(jobId);
    }

    @PutMapping("/updatejob")
    JobDTO updateJobDetails(@RequestBody JobDTO jobDTO) {
        return jobServiceIMPL.updateJobDetails(jobDTO);
    }

    @GetMapping("/getjob/{jobId}")
    JobDTO viewJobDetails(@PathVariable int jobId) {
        return jobServiceIMPL.viewJobDetails(jobId);
    }

    @GetMapping("/filterjob/{jobCategory}")
    List<JobDTO> filterJob(@PathVariable JobCategory jobCategory) {
        return jobServiceIMPL.filterJob(jobCategory);
    }

    @GetMapping("/get-job-by-user/{userId}")
    public ResponseEntity<StandardResponse> getJobsByUser(@PathVariable Long userId) {
        List<JobDetailsResponseDTO> jobs = jobServiceIMPL.getJobsByUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", jobs),
                HttpStatus.OK);
    }

}
