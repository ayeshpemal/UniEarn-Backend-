package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.request.UnfollowRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.service.FollowService;
import com.finalproject.uni_earn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("unfollow")
    public ResponseEntity<String> unfollowEmployer(@RequestBody UnfollowRequestDTO unfollowRequestDTO) {
        String responseMessage = followService.unfollowEmployer(unfollowRequestDTO.getStudentId(), unfollowRequestDTO.getEmployerId());
        return ResponseEntity.ok(responseMessage);
    }
}