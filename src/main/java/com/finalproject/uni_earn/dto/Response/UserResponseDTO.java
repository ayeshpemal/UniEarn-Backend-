package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDTO {
    @NotBlank
    private String userName;

    private String profilePictureUrl;
    @Email
    @NotBlank
    private String email;

    private Role role; // STUDENT, EMPLOYER, ADMIN

    // Additional fields for Student
    private String displayName;
    private String university;
    private String gender;
    private List<String> skills;
    private List<String> preferences;

    // Additional fields for Employer
    private String companyName;
    private String companyDetails;
    private List<String> categories;
    private Boolean verificationStatus;

    // Common fields for all roles
    private String location;
    private List<String> contactNumbers;
    private float rating;
}