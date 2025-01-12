package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateRequestDTO {
    private String address;
    private List<String> contactNumber;

    // Student-specific fields
    private String gender;
    private List<String> preferences;
    private List<String> skills;

    // Employer-specific fields
    private String companyName;
    private String companyDetails;
}
