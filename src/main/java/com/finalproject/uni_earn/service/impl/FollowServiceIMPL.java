package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Follow;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.FollowRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class FollowServiceIMPL implements FollowService {

    @Autowired
    private FollowRepo followRepo;

    @Autowired
    private UserRepo userRepo;

    public String followEmployer(Long studentId, Long employerId) {
        // Fetch the student (User acting as Student)
        User studentUser = userRepo.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!(studentUser instanceof Student student)) {
            throw new RuntimeException("The user with ID: " + studentId + " is not a student.");
        }

        // Fetch the employer (User acting as Employer)
        User employerUser = userRepo.findById(Math.toIntExact(employerId))
                .orElseThrow(() -> new NotFoundException("Employer not found with ID: " + employerId));

        if (!(employerUser instanceof Employer employer)) {
            throw new RuntimeException("The user with ID: " + employerId + " is not an employer.");
        }

        // Check if the follow relationship already exists
        if (followRepo.findByStudentAndEmployer(student, employer).isPresent()) {
            throw new RuntimeException("The student already follows this employer.");
        }

        // Create and save the follow relationship
        Follow follow = new Follow();
        follow.setStudent(student);
        follow.setEmployer(employer);
        followRepo.save(follow);

        return "Successfully followed employer with ID: " + employerId;
    }

    public String unfollowEmployer(Long studentId, Long employerId) {
        // Fetch the student (User acting as Student)
        User studentUser = userRepo.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!(studentUser instanceof Student student)) {
            throw new RuntimeException("The user with ID: " + studentId + " is not a student.");
        }

        // Fetch the employer (User acting as Employer)
        User employerUser = userRepo.findById(Math.toIntExact(employerId))
                .orElseThrow(() -> new NotFoundException("Employer not found with ID: " + employerId));

        if (!(employerUser instanceof Employer employer)) {
            throw new RuntimeException("The user with ID: " + employerId + " is not an employer.");
        }

        // Check if the follow relationship exists
        Follow follow = (Follow) followRepo.findByStudentAndEmployer(student, employer)
                .orElseThrow(() -> new RuntimeException("The student is not following this employer."));

        // Remove the follow relationship
        followRepo.delete(follow);

        return "Successfully unfollowed employer with ID: " + employerId;
    }

}