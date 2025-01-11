package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.enums.RatingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingDTO {


//    private Application application;
    private Integer ratingScore;
    private String comment;
    private LocalDateTime ratingTime;
    private RatingType type;
}
