package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.RatingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingResponseBasicDTO {
    private Long ratingId;
    private Long raterId;
    private Long ratedId;
    private Long jobId;
    private LocalDateTime createdAt;
}
