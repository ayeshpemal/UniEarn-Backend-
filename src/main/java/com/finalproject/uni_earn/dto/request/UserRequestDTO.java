package com.finalproject.uni_earn.dto.request;

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
public class UserRequestDTO {
    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Role role; // STUDENT, EMPLOYER, ADMIN

    // Additional fields for Student
    private String university;
    private String gender;
    private List<String> skills;
    private List<String> preferences;

    // Additional fields for Employer
    private String companyName;
    private Boolean verificationStatus;

    // Common fields for all roles
    private String address;
    private List<String> contactNumbers;
}
