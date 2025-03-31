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
            "AND r.application.applicationId = :applicationId")
    boolean existsByRaterRatedAndApplication(
            @Param("raterId") Long raterId,
            @Param("ratedId") Long ratedId,
            @Param("applicationId") Long applicationId
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId, r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.category, r.createdAt, t.id, t.teamName) " +
            "FROM Ratings r " +
            "JOIN r.application a " +
            "JOIN a.job j " +
            "LEFT JOIN a.team t " +
            "WHERE r.rated.userId = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<UserRatingDetailsResponseDTO> findAllRatingsReceivedByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId, r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.category, r.createdAt, t.id, t.teamName) " +
            "FROM Ratings r " +
            "JOIN r.application a " +
            "JOIN a.job j " +
            "LEFT JOIN a.team t " +
            "WHERE r.rater.userId = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<UserRatingDetailsResponseDTO> findAllRatingsGivenByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT new com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO(" +
            "r.ratingId, r.rater.userName, r.rated.userName, j.jobTitle, r.score, r.comment, " +
            "r.type, r.category, r.createdAt, t.id, t.teamName) " +
            "FROM Ratings r " +
            "JOIN r.application a " +
            "JOIN a.job j " +
            "LEFT JOIN a.team t " +
            "WHERE r.rater.userId = :userId " +
            "AND a.applicationId = :applicationId " +
            "AND r.rated.userId = :ratedId")
    Optional<UserRatingDetailsResponseDTO> findRatingByRaterAndApplicationAndRated(
            @Param("userId") Long userId,
            @Param("applicationId") Long applicationId,
            @Param("ratedId") Long ratedId
    );

    @Query("SELECT r.createdAt FROM Ratings r " +
            "WHERE r.ratingId = :ratingId")
    Optional<LocalDateTime> findCreatedAtByRatingId(
            @Param("ratingId") Long ratingId
    );

    Long countAllByRatedUserId(Long userId);
    Long countAllByRaterUserId(Long userId);

    boolean existsByRatingId(Long ratingId);

    List<Ratings> findAllByRated_UserId(long userId);
}
