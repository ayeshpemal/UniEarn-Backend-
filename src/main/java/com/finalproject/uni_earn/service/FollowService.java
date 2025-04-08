package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FollowService {
    String followEmployer(Long studentId, Long employerId);

    String unfollowEmployer(Long studentId, Long employerId);
    // void followEmployer(Student userID, Employer userId);

    List<UserDTO> getFollowingEmployers(Long studentId, Pageable pageable);

    // Method to follow a student
    void followStudent(Long studentId, Long targetStudentId);

    // Method to unfollow a student
    void unfollowStudent(Long studentId, Long targetStudentId);

    // Updated method to get all students a student is following with pagination
    List<UserDTO> getFollowingStudents(Long studentId, Pageable pageable);
}