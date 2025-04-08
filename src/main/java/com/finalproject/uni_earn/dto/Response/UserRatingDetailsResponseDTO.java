package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.RatingCategory;
import com.finalproject.uni_earn.entity.enums.RatingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRatingDetailsResponseDTO {
    private Long ratingId;
    private String raterUserName;
    private String ratedUserName;
    private String jobTitle;
    private Integer score;
    private String comment;
    private RatingType type;
    private RatingCategory category;
    private LocalDateTime createdAt;
    private Long teamId;  // Will be null for individual applications
    private String teamName; // Will be null for individual applications
}
