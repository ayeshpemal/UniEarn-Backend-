package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.RatingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRatingDetailsResponseDTO {
    private Long ratingId;
    private String raterName;
    private String ratedName;
    private String jobName;
    private Integer score;
    private String comment;
    private RatingType ratingType;
    private LocalDateTime ratingDate;
}
