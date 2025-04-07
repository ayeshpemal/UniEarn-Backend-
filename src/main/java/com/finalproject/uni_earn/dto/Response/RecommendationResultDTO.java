package com.finalproject.uni_earn.dto.Response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecommendationResultDTO {
    private int jobId;
    private String title;
    private String category;
    private double similarityScore;
}
