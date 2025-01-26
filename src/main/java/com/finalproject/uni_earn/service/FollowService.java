package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;

public interface FollowService {
    String followEmployer(Long studentId, Long employerId);

    String unfollowEmployer(Long studentId, Long employerId);
    // void followEmployer(Student userID, Employer userId);
}