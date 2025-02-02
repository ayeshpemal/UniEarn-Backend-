package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.exception.DuplicateEmailException;
import com.finalproject.uni_earn.exception.DuplicateUserNameException;
import com.finalproject.uni_earn.exception.InvalidRoleException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.EmailService;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.JwtUtil;
import com.finalproject.uni_earn.util.TokenUtil;
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
    @Autowired
    private EmailService emailService;

    @Override
    public String registerUser(UserRequestDTO userRequestDTO) {
        if(userRepo.existsByUserName(userRequestDTO.getUserName())){
            throw new DuplicateUserNameException("Username already registered: " + userRequestDTO.getUserName());
        }
        if (userRepo.existsByEmail(userRequestDTO.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + userRequestDTO.getEmail());
        }

        // Determine subclass based on role
        User user;
        switch (userRequestDTO.getRole().toString().toUpperCase()) {
            case "STUDENT":
                user = modelMapper.map(userRequestDTO, Student.class);
                break;
            case "EMPLOYER":
                user = modelMapper.map(userRequestDTO, Employer.class);
                break;
            /*case "ADMIN":
                user = modelMapper.map(userRequestDTO, Admin.class);
                break;*/
            default:
                throw new InvalidRoleException("Invalid role: " + userRequestDTO.getRole());
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        // Generate verification token
        String token = TokenUtil.generateToken();
        user.setVerificationToken(token);

        // Save user to the database
        userRepo.save(user);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), token);

        return "User registered successfully with username: " + user.getUserName() + " Please check your email to verify your account.";
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

    @Override
    public String updateUserDetails(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        // Find the existing user
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        // Check for specific subclass updates
        if (user instanceof Student student) {

            if (userUpdateRequestDTO.getGender() != null) {
                try {
                    Gender gender = Gender.valueOf(userUpdateRequestDTO.getGender().toUpperCase()); // Convert String to Enum
                    student.setGender(gender);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid gender value: " + userUpdateRequestDTO.getGender());
                }
            }
            if (userUpdateRequestDTO.getPreferences() != null) {
                student.clearPreferences(); // Clear existing preferences
                for(int i = 0; i < userUpdateRequestDTO.getPreferences().size(); i++) {
                    try {
                        JobCategory jobCategory = JobCategory.valueOf(userUpdateRequestDTO.getPreferences().get(i).toUpperCase()); // Convert String to Enum
                        student.addPreference(jobCategory);

                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid preferences value: " + userUpdateRequestDTO.getPreferences().get(i));
                    }
                }
            }
            if (userUpdateRequestDTO.getSkills() != null) {
                student.setSkills(userUpdateRequestDTO.getSkills());
            }
            if (userUpdateRequestDTO.getLocation() != null) {
                try {
                    Location location = Location.valueOf(userUpdateRequestDTO.getLocation().toUpperCase()); // Convert String to Enum
                    student.setLocation(location);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid location value: " + userUpdateRequestDTO.getLocation());
                }
            }
        } else if (user instanceof Employer employer) {

            if (userUpdateRequestDTO.getCompanyName() != null) {
                employer.setCompanyName(userUpdateRequestDTO.getCompanyName());
            }
            if (userUpdateRequestDTO.getCompanyDetails() != null) {
                employer.setCompanyDetails(userUpdateRequestDTO.getCompanyDetails());
            }
            if (userUpdateRequestDTO.getLocation() != null) {
                try {
                    Location location = Location.valueOf(userUpdateRequestDTO.getLocation().toUpperCase()); // Convert String to Enum
                    employer.setLocation(location);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid location value: " + userUpdateRequestDTO.getLocation());
                }
            }
        }

        // Save updated user
        userRepo.save(user);

        return "User updated successfully with ID: " + userId;
    }

    @Override
    public boolean verifyUser(String token) {
        User user = userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.isVerified()) {
            throw new RuntimeException("Email already verified");
        }

        user.setVerified(true);
        user.setVerificationToken(null); // Clear token after verification
        userRepo.save(user);
        return true;
    }


}
