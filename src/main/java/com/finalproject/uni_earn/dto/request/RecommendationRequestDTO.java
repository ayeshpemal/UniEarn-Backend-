package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecommendationRequestDTO {

    private long jobID;
    private String title;
    private String jobDescription;
    private String category;
    private String status;
    private String startAt;
    private String company;
}
