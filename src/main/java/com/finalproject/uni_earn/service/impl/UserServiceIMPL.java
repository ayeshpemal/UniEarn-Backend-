package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.UserService;
import org.hibernate.internal.build.AllowSysOut;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String registerUser(UserRequestDTO userRequestDTO) {
        if(userRepo.existsByUserName(userRequestDTO.getUserName())){
            throw new RuntimeException("Username already registered: " + userRequestDTO.getUserName());
        }
        if (userRepo.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("Email already registered: " + userRequestDTO.getEmail());
        }

        // Determine subclass based on role
        User user;
        switch (userRequestDTO.getRole().toString().toUpperCase()) {
            case "STUDENT":
                user = modelMapper.map(userRequestDTO, Student.class);
                break;
            /*case "EMPLOYER":
                user = modelMapper.map(userRequestDTO, Employer.class);
                break;
            case "ADMIN":
                user = modelMapper.map(userRequestDTO, Admin.class);
                break;*/
            default:
                throw new IllegalArgumentException("Invalid role: " + userRequestDTO.getRole());
        }

        // Hash the password
        //user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        // Save user to the database
        userRepo.save(user);

        return "User registered successfully with username: " + user.getUserName();
    }
}
