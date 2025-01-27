package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRatingRequestDTO {
    private Long ratingId;
    private Long raterId;
    private Long ratedId;
    private Long jobId;
    private Integer newScore;
    private String newComment;
}
