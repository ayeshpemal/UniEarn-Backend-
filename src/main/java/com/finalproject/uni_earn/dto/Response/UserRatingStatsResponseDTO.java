package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRatingStatsResponseDTO {
    private Long userId;
    private String userName;
    private Map<Integer, Long> ratingDistribution;  // Maps rating score to count
    private Double averageRating;
    private Long totalRatings;
}
