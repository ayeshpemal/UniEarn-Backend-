package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO;
import com.finalproject.uni_earn.entity.Ratings;
import com.finalproject.uni_earn.entity.enums.RatingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepo extends JpaRepository<Ratings, Long> {


    @Query("SELECT COUNT(r) > 0 FROM Ratings r " +
            "WHERE r.rater.userId = :raterId " +
            "AND r.rated.userId = :ratedId " +
            "AND r.job.jobId = :jobId ")
    boolean existsByRaterRatedAndJob(
            @Param("raterId") Long raterId,
            @Param("ratedId") Long ratedId,
            @Param("jobId") Long jobId
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId,r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.createdAt) " +
            "FROM Ratings r " +
            "JOIN r.job j " +
            "WHERE r.rated.userId = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<UserRatingDetailsResponseDTO> findAllRatingsReceivedByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId,r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.createdAt) " +
            "FROM Ratings r " +
            "JOIN r.job j " +
            "WHERE r.rater.userId = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<UserRatingDetailsResponseDTO> findAllRatingsGivenByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId,r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.createdAt) " +
            "FROM Ratings r " +
            "JOIN r.job j " +
            "WHERE r.rater.userId = :userId " +
            "AND r.job.jobId = :jobId " +
            "AND r.rated.userId = :ratedId")
    Optional<UserRatingDetailsResponseDTO> findRatingByRaterAndJobAndRated(
            @Param("userId") Long userId,
            @Param("jobId") Long jobId,
            @Param("ratedId") Long ratedId
    );

    @Query("SELECT r.createdAt FROM Ratings r " +
            "WHERE r.ratingId = :ratingId " )
    Optional<LocalDateTime> findCreatedAtByRatingId(
            @Param("ratingId") Long ratingId
    );

    Long countAllByRatedUserId(Long userId);
    Long countAllByRaterUserId(Long userId);

    boolean existsByRatingId(Long ratingId);






}
