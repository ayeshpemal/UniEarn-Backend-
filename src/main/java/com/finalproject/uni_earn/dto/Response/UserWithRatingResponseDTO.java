package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserWithRatingResponseDTO {
    private Long userId;
    private String userName;
    private Double averageRating;
    private String userType;
}
