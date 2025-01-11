package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
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

}
