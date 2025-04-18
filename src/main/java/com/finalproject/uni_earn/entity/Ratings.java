package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.RatingCategory;
import com.finalproject.uni_earn.entity.enums.RatingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "ratings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"application_id", "rater_id", "rated_id", "rating_type"})
        },
        indexes = {
                @Index(name = "idx_rating_application", columnList = "application_id"),
                @Index(name = "idx_rating_identity", columnList = "rater_id, rated_id, rating_type"),
                @Index(name = "idx_rated_user", columnList = "rated_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ratings extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_id", nullable = false)
    private User rated;

    @Min(1)
    @Max(5)
    @Column(name = "rating_score", nullable = false)
    private Integer score;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type", nullable = false, length = 20)
    private RatingType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category", nullable = false, length = 30)
    private RatingCategory category;


}

