package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
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

    @PutMapping("/updatejob")
    JobDTO updateJobDetails(@RequestBody JobDTO jobDTO){
        return jobServiceIMPL.updateJobDetails(jobDTO);
    }
    @DeleteMapping("/deletejob/{jobId}")
    String deleteJob(@PathVariable int jobId){
        return jobServiceIMPL.deleteJob(jobId);
    }
    @GetMapping("/getjob/{jobId}")
    JobDTO viewJobDetails(@PathVariable int jobId){
        return jobServiceIMPL.viewJobDetails(jobId);
    }
    @GetMapping("/filterjob/{category}")
    List<JobDTO> filterJob(@PathVariable String category){
        return jobServiceIMPL.filterJob(category);
    }
    @PostMapping("/addjob")
    public JobDTO addJob(@RequestBody JobDTO jobDTO) {
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiii");
        return jobServiceIMPL.addJob(jobDTO);
    }
}
