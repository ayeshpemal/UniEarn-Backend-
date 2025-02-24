package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.AddJobRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateJobRequestDTO;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.service.JobService;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/jobs/")
public class JobController {

    @Autowired
    JobService jobService;

    //@PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/addjob")
    public ResponseEntity<StandardResponse> addJob(@RequestBody AddJobRequestDTO addJobRequestDTO) {
        String message = jobService.addJob(addJobRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success..", message),
                HttpStatus.CREATED
        );
    }

    //@PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @DeleteMapping("/deletejob/{jobId}")
    public ResponseEntity<StandardResponse> deleteJob(@PathVariable Long jobId) {
        String message = jobService.deleteJob(jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", message),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/updatejob/{id}")
    public ResponseEntity<StandardResponse> updateJob(@RequestBody UpdateJobRequestDTO updateJobRequestDTO) {
        String message = jobService.updateJob(updateJobRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success.", message),
                HttpStatus.CREATED
        );
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/getjob/{jobId}")
    ResponseEntity<StandardResponse> viewJobDetails(@PathVariable Long jobId) {
        JobDTO job = jobService.viewJobDetails(jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", job),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    //search by category
    @GetMapping( path = "/jobsbycategory", params = {"jobcategory","page"})
    ResponseEntity<StandardResponse> JobsByCategory( @RequestParam(value = "jobcategory") JobCategory jobCategory, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobService.filterJobByCategory(jobCategory,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    //search by location
    @GetMapping(path = "/jobsbylocation", params = {"location","page"})
    ResponseEntity<StandardResponse> JobsByLocation(@RequestParam(value = "location") Location location, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobService.SearchJobByLocation(location,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    //search by keyword
    @GetMapping(path = "/searchjobsbykeyword", params = {"keyword","page"})
    ResponseEntity<StandardResponse> searchJobKeyword(@RequestParam(value = "keyword") String keyWord, @RequestParam(value = "page") Integer page){
        PaginatedResponseJobDTO jobList = jobService.searchJobByKeyword(keyWord, page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", jobList),
                HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('STUDENT')")
    //student sugessions
    @GetMapping(path = "/studentpreferedjobs", params = {"student_id","page"})//this jobs are shown to students
    ResponseEntity<StandardResponse> StudentPreferedJobs(@RequestParam(value = "student_id") long studentId, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobService.studentJobs(studentId,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('EMPLOYER') or hasRole('STUDENT') or hasRole('ADMIN')")
    //jobs posted by user or completed by user
    @GetMapping(path = "/get-jobs-by-user", params = {"user_id","page"})
    ResponseEntity<StandardResponse> getJobsByUser(@RequestParam(value = "user_id") long userId, @RequestParam(value = "page") Integer page) {
        List<JobDetailsResponseDTO> jobList = jobService.getJobsByUser(userId,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", jobList),
                HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    //search by location,categories,keywords
    @GetMapping("/search")
    ResponseEntity<StandardResponse> searchJobs(
            @RequestParam(required = false) Location location,
            @RequestParam(required = false) String categories, // List of categories as CSV
            @RequestParam(required = false) String keyword,
            @RequestParam Integer page) {

        List<JobCategory> categoryList = (categories != null && !categories.isEmpty()) ?
                Arrays.stream(categories.split(",")).map(JobCategory::valueOf).collect(Collectors.toList()) : null;

        PaginatedResponseJobDTO jobList = jobService.searchJobs(location, categoryList, keyword,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", jobList),
                HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @PutMapping(path = "/set-status", params = {"job_id","status"})
    public ResponseEntity<StandardResponse> setStatus(@RequestParam(value = "job_id") long jobId, @RequestParam(value = "status") boolean status) {
        String message = jobService.setStatus(jobId, status);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success.", message),
                HttpStatus.CREATED
        );
    }
}
