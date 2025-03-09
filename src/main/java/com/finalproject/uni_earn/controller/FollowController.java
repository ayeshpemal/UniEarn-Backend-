package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.dto.request.UnfollowRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.service.FollowService;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follows")
public class FollowController {
    @Autowired
    FollowService followService;

    @Autowired
    private UserService userService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    //@PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{employerId}/follow")
    public ResponseEntity<String> followEmployer(
            @RequestParam Long studentId,
            @PathVariable Long employerId) {
        String responseMessage = followService.followEmployer(studentId, employerId);
        return ResponseEntity.ok(responseMessage);
    }

    //@PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("unfollow")
    public ResponseEntity<String> unfollowEmployer(
            @RequestParam Long studentId,
            @RequestParam Long employerId) {
        String responseMessage = followService.unfollowEmployer(studentId, employerId);
        return ResponseEntity.ok(responseMessage);
    }

    //@PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{studentId}/followingemployers")
    public ResponseEntity<StandardResponse> getFollowingEmployers(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Successfully retrieved following employers.", followService.getFollowingEmployers(studentId, PageRequest.of(page, size))),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT')")
    // Endpoint for following another student
    @PostMapping("/{studentId}/followstudents/{targetStudentId}")
    public ResponseEntity<StandardResponse> followStudent(@PathVariable Long studentId, @PathVariable Long targetStudentId) {
        followService.followStudent(studentId, targetStudentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Successfully followed student.", null),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT')")
    // Endpoint for unfollowing another student
    @DeleteMapping("/{studentId}/unfollowstudents/{targetStudentId}")
    public ResponseEntity<StandardResponse> unfollowStudent(@PathVariable Long studentId, @PathVariable Long targetStudentId) {
        followService.unfollowStudent(studentId, targetStudentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Successfully unfollowed student.", null),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('STUDENT')")
    // Endpoint for getting all students that the current student is following with pagination
    @GetMapping("/{studentId}/followingstudents")
    public ResponseEntity<StandardResponse> getFollowingStudents(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Successfully retrieved following students.", followService.getFollowingStudents(studentId, PageRequest.of(page, size))),
                HttpStatus.OK
        );
    }
}