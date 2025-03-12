package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.exception.*;
import com.finalproject.uni_earn.repo.JobRepo;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.JwtUtil;
import com.finalproject.uni_earn.util.PasswordValidator;
import com.finalproject.uni_earn.util.TokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class  UserServiceIMPL implements UserService {
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
    @Autowired
    private JobRepo jobRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private S3Service s3Service;

    @Override
    public String registerUser(UserRequestDTO userRequestDTO) {
        if(userRepo.existsByUserName(userRequestDTO.getUserName())){
            throw new DuplicateUserNameException("Username already registered: " + userRequestDTO.getUserName());
        }
        if (userRepo.existsByEmail(userRequestDTO.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + userRequestDTO.getEmail());
        }

        // Validate password complexity
        if (!PasswordValidator.isValidPassword(userRequestDTO.getPassword())) {
            throw new InvalidValueException("Password does not meet complexity requirements");
        }

        // Determine subclass based on role
        User user = switch (userRequestDTO.getRole().toString().toUpperCase()) {
            case "STUDENT" -> modelMapper.map(userRequestDTO, Student.class);
            case "EMPLOYER" -> modelMapper.map(userRequestDTO, Employer.class);
            //case "ADMIN" -> modelMapper.map(userRequestDTO, User.class);
            default -> throw new InvalidValueException("Invalid role: " + userRequestDTO.getRole());
        };

        // Hash the password
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        // Generate verification token
        String token = TokenUtil.generateToken();
        user.setVerificationToken(token);

        // Save user to the database
        userRepo.save(user);

        // Send verification email
        String verifyUrl = "http://localhost:8100/api/user/verify?token=" + token;
        String emailBody = "Please click the following link to verify your email: " + verifyUrl;
        emailService.sendEmail(user.getEmail(), "Verify Your Email", emailBody);

        return "User registered successfully with username: " + user.getUserName() + " Please check your email to verify your account.";
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassword())
            );
        }catch (BadCredentialsException e) {
            throw new InvalidValueException("Invalid email or password");
        }

        User user = userRepo.findByUserNameAndIsDeletedFalse(loginRequestDTO.getUserName())
                .orElseThrow(() -> new NotFoundException("User not found with user name: " + loginRequestDTO.getUserName()));

        if(!user.isVerified()){
            throw new UserNotVerifiedException("Email not verified");
        }
        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return new LoginResponseDTO(token, "Login successful");
    }

    @Override
    public UserResponseDTO getUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        String profilePictureUrl = user.getProfilePictureUrl() != null
                ? s3Service.generatePresignedUrl(user.getProfilePictureUrl())
                : null;

        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        userResponseDTO.setProfilePictureUrl(profilePictureUrl);
        return userResponseDTO;
    }



    @Override
    public String updateUserDetails(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        // Find the existing user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        // Check for specific subclass updates
        if (user instanceof Student student) {

            if (userUpdateRequestDTO.getDisplayName() != null) {
                student.setDisplayName(userUpdateRequestDTO.getDisplayName());
            }

            if (userUpdateRequestDTO.getGender() != null) {
                try {
                    Gender gender = Gender.valueOf(userUpdateRequestDTO.getGender().toUpperCase()); // Convert String to Enum
                    student.setGender(gender);
                } catch (IllegalArgumentException e) {
                    throw new InvalidValueException("Invalid gender value: " + userUpdateRequestDTO.getGender());
                }
            }
            if (userUpdateRequestDTO.getPreferences() != null) {
                student.clearPreferences(); // Clear existing preferences
                for(int i = 0; i < userUpdateRequestDTO.getPreferences().size(); i++) {
                    try {
                        JobCategory jobCategory = JobCategory.valueOf(userUpdateRequestDTO.getPreferences().get(i).toUpperCase()); // Convert String to Enum
                        student.addPreference(jobCategory);

                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid preferences value: " + userUpdateRequestDTO.getPreferences().get(i));
                    }
                }
            }
            if(userUpdateRequestDTO.getContactNumber() != null){
                student.clearContactNumbers(); // Clear existing contact numbers
                for(int i = 0; i < userUpdateRequestDTO.getContactNumber().size(); i++) {
                    student.addContactNumber(userUpdateRequestDTO.getContactNumber().get(i));
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
                    throw new InvalidValueException("Invalid location value: " + userUpdateRequestDTO.getLocation());
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
                    throw new InvalidValueException("Invalid location value: " + userUpdateRequestDTO.getLocation());
                }
            }
        }

        // Save updated user
        userRepo.save(user);

        return "User updated successfully with ID: " + userId;
    }

    /**
     * Uploads and sets a user's profile picture.
     */
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOptional.get();
        String fileName = s3Service.uploadFile(file);
        user.setProfilePictureUrl(fileName);
        userRepo.save(user);
        System.out.println("Profile picture uploaded successfully for user: " + userId);

        return s3Service.generatePresignedUrl(fileName);
    }

    /**
     * Retrieves the pre-signed URL for a user's profile picture.
     */
    public String getProfilePicture(Long userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOptional.get();
        if (user.getProfilePictureUrl() == null) {
            throw new RuntimeException("No profile picture uploaded!");
        }

        return s3Service.generatePresignedUrl(user.getProfilePictureUrl());
    }

    @Override
    public String verifyUser(String token) {
        User user = userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidValueException("Invalid or expired token"));

        if (user.isVerified()) {
            throw new RuntimeException("Email already verified");
        }

        user.setVerified(true);
        user.setVerificationToken(null); // Clear token after verification
        userRepo.save(user);
        String url = null;
        if(user.getRole() == Role.STUDENT) {
            url = "http://localhost:3000/verify?userId=" + user.getUserId();
        }
        if (user.getRole() == Role.EMPLOYER){
            url = "http://localhost:3000/sign-in";
        }
        return url;
    }

    @Override
    public String resendVerificationEmail(String username) {
        // Send verification email
        User user = userRepo.findByUserNameAndIsDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        String token = null;
        if(!user.isVerified()){
            if(user.getVerificationToken() != null) {
                token = user.getVerificationToken();
            }else {
                throw new InvalidValueException("No verification token found");
            }
        }else{
            throw new InvalidValueException("User is already verified");
        }

        String verifyUrl = "http://localhost:8100/api/user/verify?token=" + token;
        String emailBody = "Please click the following link to verify your email: " + verifyUrl;
        emailService.sendEmail(user.getEmail(), "Verify Your Email", emailBody);
        return "Please check your email to verify your account.";
    }

    @Override
    public String deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        if (user instanceof Employer employer) {
            Optional<Job> jobs = jobRepo.findAllByEmployer(employer);
            jobs.ifPresent(job -> {
                jobRepo.setActiveState(job.getJobId());
            });
        }

        user.setDeleted(true);
        userRepo.save(user);
        return "User deleted successfully with ID: " + userId;
    }

    @Override
    public String restoreUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setDeleted(false);
        userRepo.save(user);
        return "User restored successfully with ID: " + userId;
    }


    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // Validate user existence
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidValueException("Old password is incorrect");
        }

        // Validate new password complexity
        if (!PasswordValidator.isValidPassword(newPassword)) {
            throw new InvalidValueException("Password does not meet complexity requirements");
        }

        // Hash the new password
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update the user's password
        user.setPassword(hashedPassword);
        userRepo.save(user);
    }


}
