package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfirmJobDTO {
    private Long id;
    private String companyName;
    private String description;
    private String date;
    private String time;
    private String gender;
    private int requiredWorkers;
    private String salary;
    private String companyLogo; // Added
    private String coverImage; // Added
}
