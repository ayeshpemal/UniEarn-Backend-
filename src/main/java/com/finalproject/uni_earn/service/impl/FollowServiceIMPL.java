package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.UserDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Follow;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.repo.FollowRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.FollowService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowServiceIMPL implements FollowService {

    @Autowired
    private FollowRepo followRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepository;
    @Autowired
    private ModelMapper modelMapper;

    public String followEmployer(Long studentId, Long employerId) {
        // Fetch the student (User acting as Student)
        User studentUser = userRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!(studentUser instanceof Student student)) {
            throw new RuntimeException("The user with ID: " + studentId + " is not a student.");
        }

        // Fetch the employer (User acting as Employer)
        User employerUser = userRepo.findById(employerId)
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
        User studentUser = userRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));

        if (!(studentUser instanceof Student student)) {
            throw new RuntimeException("The user with ID: " + studentId + " is not a student.");
        }

        // Fetch the employer (User acting as Employer)
        User employerUser = userRepo.findById(employerId)
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

    @Override
    public List<UserDTO> getFollowingEmployers(Long studentId, Pageable pageable) {
        // Fetch the student (User acting as Student)
        User studentUser = userRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentId));
        Page<Employer> employers = followRepo.findFollowingEmployersByStudentId(studentId, pageable);

        return employers.getContent().stream()
                .map(student1 -> modelMapper.map(student1, UserDTO.class))
                .toList();
    }


    @Override
    @Transactional
    public void followStudent(Long studentId, Long targetStudentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Student targetStudent = studentRepository.findById(targetStudentId)
                .orElseThrow(() -> new RuntimeException("Target student not found"));

        if(student.getFollowing().contains(targetStudent)){
            throw new AlreadyExistException("Already following this student");
        }

        student.followStudent(targetStudent); // Add to following and followers
        studentRepository.save(student); // Save changes to student
        studentRepository.save(targetStudent); // Save changes to target student
    }

    @Override
    @Transactional
    public void unfollowStudent(Long studentId, Long targetStudentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Student targetStudent = studentRepository.findById(targetStudentId)
                .orElseThrow(() -> new RuntimeException("Target student not found"));

        if (!student.getFollowing().contains(targetStudent)) {
            throw new NotFoundException("Not following this student");
        }

        student.unfollowStudent(targetStudent); // Remove from following and followers
        studentRepository.save(student); // Save changes to student
        studentRepository.save(targetStudent); // Save changes to target student
    }

    @Override
    public List<UserDTO> getFollowingStudents(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Use the Pageable object to return a paginated result
        Page<Student> students = studentRepository.findFollowingByStudentId(studentId, pageable);

        return students.getContent().stream()
                .map(student1 -> modelMapper.map(student1, UserDTO.class))
                .toList();
    }
}