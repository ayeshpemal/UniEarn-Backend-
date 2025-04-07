package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedCategoryStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobStaticsDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedJobSummeryDTO;
import com.finalproject.uni_earn.dto.Response.EmployerBriefSummaryDTO;
import com.finalproject.uni_earn.entity.enums.JobStatus;
import com.finalproject.uni_earn.exception.UnauthorizedActionException;
import com.finalproject.uni_earn.exception.UserNotFoundException;
import com.finalproject.uni_earn.service.EmployerAnalysisService;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/employer/analysis")
@Validated
public class EmployerAnalysisController {

    private final EmployerAnalysisService employerAnalysisService;

    @Autowired
    public EmployerAnalysisController(EmployerAnalysisService employerAnalysisService) {
        this.employerAnalysisService = employerAnalysisService;
    }
    /*
    * this is for getting all the job summaries for the employer,
    * this can use when there is no filters like date or status or category
    * */

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/jobs/all/{employerId}")
    public ResponseEntity<StandardResponse> getAllJobSummeryForEmployer(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            PaginatedJobSummeryDTO result = employerAnalysisService.getAllJobSummeryForEmployer(employerId, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved job summaries", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving job summaries: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting all the job summaries for the employer filtered by date range,
    * this can use when there is a date range filter
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/jobs/date-range/{employerId}")
    public ResponseEntity<StandardResponse> getJobsByEmployerAndDateRange(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (startDate.after(endDate)) {
            return ResponseEntity.badRequest()
                    .body(new StandardResponse(400, "Start date must be before or equal to end date", null));
        }

        try {
            PaginatedJobSummeryDTO result = employerAnalysisService.getJobsByEmployerAndDateRange(employerId, startDate, endDate, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved job summaries by date range", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving job summaries: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting all the job summaries for the employer filtered by job status,
    * this can use when there is a job status filter
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/jobs/status/{employerId}")
    public ResponseEntity<StandardResponse> getJobsByEmployerAndStatus(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam @NotNull JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            PaginatedJobSummeryDTO result = employerAnalysisService.getJobsByEmployerAndStatus(employerId, status, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved job summaries by status", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving job summaries: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting all the job summaries for the employer filtered by both job status and date range,
    * this can use when there is a job status and date range filter
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/jobs/status-date-range/{employerId}")
    public ResponseEntity<StandardResponse> getJobsByEmployerStatusAndDateRange(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam @NotNull JobStatus status,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (startDate.after(endDate)) {
            return ResponseEntity.badRequest()
                    .body(new StandardResponse(400, "Start date must be before or equal to end date", null));
        }

        try {
            PaginatedJobSummeryDTO result = employerAnalysisService.getJobsByEmployerStatusAndDateRange(employerId, status, startDate, endDate, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved job summaries by status and date range", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving job summaries: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting the jobs with most applications by the employer,
    * this can use when the employer wants to see the jobs with most applications
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/applications/most/{employerId}")
    public ResponseEntity<StandardResponse> getJobsWithMostApplicationsByEmployerId(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            PaginatedJobStaticsDTO result = employerAnalysisService.getJobsWithMostApplicationsByEmployerId(employerId, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved jobs with most applications", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving application statistics: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting the jobs with least applications by the employer,
    * this can use when the employer wants to see the jobs with least applications
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/applications/least/{employerId}")
    public ResponseEntity<StandardResponse> getJobsWithLeastApplicationsByEmployerId(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            PaginatedJobStaticsDTO result = employerAnalysisService.getJobsWithLeastApplicationsByEmployerId(employerId, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved jobs with least applications", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving application statistics: " + e.getMessage(), null));
        }
    }

    /*
    * this is for getting the most popular job categories by the employer,
    * this can use when the employer wants to see the most popular job categories
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/categories/popular/{employerId}")
    public ResponseEntity<StandardResponse> getMostPopularJobCategoriesByEmployerId(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            PaginatedCategoryStaticsDTO result = employerAnalysisService.getMostPopularJobCategoriesByEmployerId(employerId, page, size);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved popular job categories", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving category statistics: " + e.getMessage(), null));
        }
    }
    /*
    * this for getting brief summary of the employer. no filtering
    * last function asked
    *
    * */
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/brief-summary/{employerId}")
    public ResponseEntity<StandardResponse> getBriefSummary(
            @PathVariable @NotNull @Min(0) Long employerId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        try {
            EmployerBriefSummaryDTO result = employerAnalysisService.getBriefSummary(employerId, startDate, endDate);
            return ResponseEntity.ok(
                    new StandardResponse(200, "Successfully retrieved employer job summary", result)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Error retrieving employer job summary: " + e.getMessage(), null));
        }
    }

}
