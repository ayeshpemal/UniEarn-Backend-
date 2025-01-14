package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedResponseJobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.Response.JobDetailsResponseDTO;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.service.impl.JobServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jdk.jfr.Category;
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
    public ResponseEntity<StandardResponse> addJob(@RequestBody JobRequestDTO jobRequestDTO) {
        String message = jobServiceIMPL.addJob(jobRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success..", message),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/deletejob/{jobId}")
    public ResponseEntity<StandardResponse> deleteJob(@PathVariable Long jobId) {
        String message = jobServiceIMPL.deleteJob(jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", message),
                HttpStatus.OK
        );
    }

    @PutMapping("/updatejob")
    public ResponseEntity<StandardResponse> updateJob(@RequestBody JobDTO jobDTO) {
        String message = jobServiceIMPL.updateJob(jobDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success.", message),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/getjob/{jobId}")
    ResponseEntity<StandardResponse> viewJobDetails(@PathVariable Long jobId) {
        JobDTO job = jobServiceIMPL.viewJobDetails(jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", job),
                HttpStatus.OK
        );
    }

    @GetMapping( path = "/filterbyjobcategory", params = {"jobcategory","page"})
    ResponseEntity<StandardResponse> filterJobByCategory( @RequestParam(value = "jobcategory") JobCategory jobCategory, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobServiceIMPL.filterJobByCategory(jobCategory,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/searchjobsbylocation", params = {"location","page"})
    ResponseEntity<StandardResponse> SearchJobByLocation(@RequestParam(value = "location") Location location, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobServiceIMPL.SearchJobByLocation(location,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/searchjobsbylocationandcategories", params = {"location", "categorylist","page"})
    ResponseEntity<StandardResponse> SearchJobsByLocationAndCategories(@RequestParam(value = "location") Location location, @RequestParam(value = "categorylist") List<JobCategory> categoryList, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobServiceIMPL.SearchJobsByLocationAndCategories(location,categoryList,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/studentjobs", params = {"student_id","page"})//this jobs are shown to students
    ResponseEntity<StandardResponse> StudentJobs(@RequestParam(value = "student_id") long studentId, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobServiceIMPL.studentJobs(studentId,page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/employerjobs", params = {"employer_id","page"})//this jobs are belongs to employer
    ResponseEntity<StandardResponse> EmployerJobs(@RequestParam(value = "employer_id") Long employerId, @RequestParam(value = "page") Integer page) {
        PaginatedResponseJobDTO jobList = jobServiceIMPL.employerJobs(employerId, page);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success.", jobList),
                HttpStatus.OK
        );
    }


//    StudentJobs(u_id)========>
//    SearchJobsByLocation(location)==============>
//    SearchJobsByLocationsAndCategories(location,List<category>)===============>
//    AddJobAndGetStudentListForNotifications(jobDTO)=====================>
//    ModifyJob(jobDTO)===============>
//    DeleteJob(j_id)===============>
//    GetJob(j_id)============>
//    EmployerJobs(e_id)============>
//    GetJobByCategory(category)========>


    @GetMapping("/get-job-by-user/{userId}")
    public ResponseEntity<StandardResponse> getJobsByUser(@PathVariable Long userId) {
        List<JobDetailsResponseDTO> jobs = jobServiceIMPL.getJobsByUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", jobs),
                HttpStatus.OK);
    }
}
