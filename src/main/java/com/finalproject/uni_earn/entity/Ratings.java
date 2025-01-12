package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.RatingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ratings {
    @Id
    @GeneratedValue
    @Column(name = "rating_id",length = 200)
    private Long rating;

//    @OneToMany
//    private Application application;

    @Column(name = "rating_score",length = 20,nullable = false)
    private Integer ratingScore;

    @Column(name = "comment",length = 2000,nullable = false)
    private String comment;

    @Column(name = "rating_time",nullable = false,updatable = false)
    private LocalDateTime ratingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",length = 100,nullable = false)
    private RatingType type;



}
