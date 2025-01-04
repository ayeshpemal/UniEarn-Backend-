package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.JwtUtil;
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
    @Autowired
    private JwtUtil jwtUtil;

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
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        // Save user to the database
        userRepo.save(user);

        return "User registered successfully with username: " + user.getUserName();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepo.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return new LoginResponseDTO(token, "Login successful");
    }
}
