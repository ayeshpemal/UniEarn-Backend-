package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Response.AddJobResponce;
import com.finalproject.uni_earn.dto.request.JobRequestDTO;
import com.finalproject.uni_earn.dto.request.SearchJobRequestDTO;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
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
    public AddJobResponce addJob(@RequestBody JobRequestDTO jobRequestDTO) {
        return jobServiceIMPL.addJob(jobRequestDTO);
    }

    @DeleteMapping("/deletejob/{jobId}")
    String deleteJob(@PathVariable Long jobId) {
        return jobServiceIMPL.deleteJob(jobId);
    }

    @PutMapping("/updatejob")
    String updateJobDetails(@RequestBody JobDTO jobDTO) {
        return jobServiceIMPL.updateJobDetails(jobDTO);
    }

    @GetMapping("/getjob/{jobId}")
    JobDTO viewJobDetails(@PathVariable Long jobId) {
        return jobServiceIMPL.viewJobDetails(jobId);
    }

    @GetMapping("/filterjob/{jobCategory}")
    List<JobDTO> filterJob(@PathVariable JobCategory jobCategory) {
        return jobServiceIMPL.filterJob(jobCategory);
    }

    @GetMapping("/searchjobsbylocation/{location}")
    List<JobDTO> SearchJobByLocation(@PathVariable Location location) {
        return jobServiceIMPL.findAllByJobLocationsContaining(location);
    }

    @GetMapping("/searchjobsbylocationandcategories")
    List<JobDTO> SearchJobsByLocationsAndCategories(@RequestBody SearchJobRequestDTO search_job_request_dto) {
        return jobServiceIMPL.findAllByJobLocationsContainingAndJobCategoryIn(search_job_request_dto);
    }

    @GetMapping("/studentjobs/{student_id}")//this jobs are shown to students
    List<JobDTO> StudentJobs(@PathVariable Long student_id) {
        return jobServiceIMPL.studentJobs(student_id);
    }

    @GetMapping("/employerjobs/{employer_id}")//this jobs are belongs to employer
    List<JobDTO> EmployerJobs(@PathVariable Long employer_id) {
        return jobServiceIMPL.employerJobs(employer_id);
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

}
