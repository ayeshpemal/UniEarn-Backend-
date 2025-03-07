package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentResponseDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.dto.StudentDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.FollowRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceIMPL implements StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ModelMapper modelMapper;

    private static final int PAGE_SIZE = 10; // Fixed page size
    @Autowired
    private UserRepo userRepo;

    @Override
    public PaginatedUserResponseDTO getAllStudents(int page) {
        Page<Student> studentPage = studentRepo.findAll(PageRequest.of(page, PAGE_SIZE));
        List<UserResponseDTO> employers =studentPage.getContent()
                .stream()
                .map(student -> modelMapper.map(student, UserResponseDTO.class))
                .collect(Collectors.toList());

        long totalStudents = studentRepo.count();

        return new PaginatedUserResponseDTO(employers, totalStudents);
    }

    @Override
    public PaginatedStudentResponseDTO searchStudents(String query, int page) {
        Page<Student> studentPage = studentRepo.findByDisplayNameContainingIgnoreCase(query, PageRequest.of(page, PAGE_SIZE));
        List<StudentDTO> students = studentPage.getContent()
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());

        long totalStudents = studentRepo.countByDisplayNameContainingIgnoreCase(query);

        return new PaginatedStudentResponseDTO(students, totalStudents);
    }

    @Override
    public PaginatedStudentResponseDTO searchStudentsWithFollowStatus(Long userId, String query, int page) {
        // Fetch the current student (the user making the request)
        Student currentStudent = studentRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + userId));

        // Perform the search operation (assuming searchStudents method returns paginated results)
        PaginatedStudentResponseDTO responseDTO = searchStudents(query, page);

        // Get the list of students from the response DTO
        List<StudentDTO> studentDTOList = responseDTO.getStudents();

        // Iterate over the student list to check the follow status
        for (StudentDTO studentDTO : studentDTOList) {
            // Fetch the student entity from the database (you might want to use a custom query to optimize this)
            Student student = studentRepo.findById(studentDTO.getUserId())
                    .orElseThrow(() -> new NotFoundException("Student not found with ID: " + studentDTO.getUserId()));

            // Check if the current student follows this student
            boolean isFollow = currentStudent.getFollowing().contains(student);

            // Set the follow status in the DTO
            studentDTO.setFollow(isFollow);
        }

        // Set the updated list of students back to the response DTO
        responseDTO.setStudents(studentDTOList);

        return responseDTO;
    }



}
