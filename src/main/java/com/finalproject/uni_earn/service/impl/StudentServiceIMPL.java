package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Paginated.PaginatedUserResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.repo.StudentRepo;
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
}
