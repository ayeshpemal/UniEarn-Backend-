package com.finalproject.uni_earn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinishedJobDTO {
    private String jobTitle;
    private String jobDescription;
    private String employerName;
}
