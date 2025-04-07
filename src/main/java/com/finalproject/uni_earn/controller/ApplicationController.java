package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedGroupApplicationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentApplicationDTO;
import com.finalproject.uni_earn.dto.Response.GroupApplicationDTO;
import com.finalproject.uni_earn.dto.Response.StudentApplicationDTO;
import com.finalproject.uni_earn.dto.Response.StudentApplicationResponseDTO;
import com.finalproject.uni_earn.dto.request.StudentSummaryRequestDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.ApplicationService;
import com.finalproject.uni_earn.service.impl.ApplicationServiceIMPL;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/application/")
public class ApplicationController {
    @Autowired
    ApplicationServiceIMPL applicationService;
    @Autowired
    UserRepo userRepository;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/apply/student")
    public ResponseEntity<StandardResponse> applyAsStudent(@RequestParam Long studentId, @RequestParam Long jobId) {
        String message = applicationService.applyAsStudent(studentId,jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "success", message),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/apply/team")
    public ResponseEntity<StandardResponse> applyAsTeam(@RequestParam Long teamId, @RequestParam Long jobId) {
        String message = applicationService.applyAsTeam(teamId, jobId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success", message),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<StandardResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus newStatus,
            @RequestParam("userId") Long userId) {

        // Assuming you have a method to fetch the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        try {
            applicationService.updateStatus(applicationId, newStatus, user);
            return ResponseEntity.ok(new StandardResponse(200, "Success", "Application status updated successfully"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StandardResponse(404, e.getMessage(), null));
        } catch (InvalidValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StandardResponse(400, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StandardResponse(500, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/viewApplicationDetails/{applicationId}")
    public ResponseEntity<StandardResponse> viewApplicationDetails(@PathVariable Long applicationId) {
        // Call the service to fetch application details
        ApplicationDTO applicationDTO = applicationService.viewApplicationDetails(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", applicationDTO),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @DeleteMapping("/deleteApplication/{applicationId}")
    public ResponseEntity<StandardResponse> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", "Application deleted successfully"),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @PostMapping("/student/summary")
    public ResponseEntity<StandardResponse> getStudentApplicationsSummary(@RequestBody StudentSummaryRequestDTO requestDTO) {

        Map<String, Object> summary = applicationService.getStudentApplicationsSummary(requestDTO);


        return ResponseEntity.ok(new StandardResponse(200, "Success", summary));
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("pending-group/job/{jobId}")
    public ResponseEntity<StandardResponse> getGroupApplicationsByJobId(@PathVariable Long jobId) {
        List<GroupApplicationDTO> groupApplications = applicationService.getGroupApplicationsByJobId(jobId);
        StandardResponse response = new StandardResponse(200, "Success", groupApplications);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("pending-student/job/{jobId}")
    public ResponseEntity<StandardResponse> getPendingStudentsByJobId(@PathVariable Long jobId) {
        List<StudentApplicationDTO> studentApplications = applicationService.getPendingStudentsByJobId(jobId);
        StandardResponse response = new StandardResponse(200, "Success", studentApplications);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/has-applied")
    public ResponseEntity<StandardResponse> hasStudentApplied(
            @RequestParam Long studentId, @RequestParam Long jobId) {

        StudentApplicationResponseDTO studentApplicationResponseDTO = applicationService.hasStudentAppliedForJob(studentId, jobId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Check completed", studentApplicationResponseDTO),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/group-applications/job/{jobId}")
    public ResponseEntity<StandardResponse> getPaginatedGroupApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        PaginatedGroupApplicationDTO paginatedGroupApplications = applicationService.getPaginatedGroupApplicationsByJobId(jobId, page, pageSize);
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", paginatedGroupApplications),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/student-applications/job/{jobId}")
    public ResponseEntity<StandardResponse> getPaginatedStudentApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        PaginatedStudentApplicationDTO paginatedStudentApplications = applicationService.getPaginatedStudentApplicationsByJobId(jobId, page, pageSize);
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", paginatedStudentApplications),
                HttpStatus.OK
        );
    }
}

