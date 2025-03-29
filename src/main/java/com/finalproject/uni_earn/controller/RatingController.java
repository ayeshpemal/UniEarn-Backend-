package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedRatingDTO;
import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.dto.request.ReatingRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateRatingRequestDTO;
import com.finalproject.uni_earn.service.RatingService;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/rating")
@Validated
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Creates a new rating
     */
    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createRating(
            @Valid @RequestBody ReatingRequestDTO ratingRequestDTO) {

        if (ratingRequestDTO.getScore() == null || ratingRequestDTO.getScore() < 1 || ratingRequestDTO.getScore() > 5) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse(400, "Score must be between 1 and 5", null));
        }

        RatingResponseBasicDTO response = ratingService.saveRating(ratingRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new StandardResponse(201, "Rating created successfully", response));
    }

    /**
     * Updates an existing rating
     */
    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateRating(
            @Valid @RequestBody UpdateRatingRequestDTO updateRatingRequestDTO) {

        // Validate score is within reasonable range
        if (updateRatingRequestDTO.getNewScore() == null ||
                updateRatingRequestDTO.getNewScore() < 1 ||
                updateRatingRequestDTO.getNewScore() > 5) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse(400, "Score must be between 1 and 5", null));
        }

        RatingResponseBasicDTO response = ratingService.updateRating(updateRatingRequestDTO);
        return ResponseEntity.ok(
                new StandardResponse(200, "Rating updated successfully", response));
    }

    /**
     * Deletes an existing rating
     * dont send rating or comment , i just put that since it is easy 
     */
    @DeleteMapping("/delete")
    public ResponseEntity<StandardResponse> deleteRating(
            @Valid @RequestBody UpdateRatingRequestDTO updateRatingRequestDTO) {

        RatingResponseBasicDTO response = ratingService.deleteRating(updateRatingRequestDTO);
        return ResponseEntity.ok(
                new StandardResponse(200, "Rating deleted successfully", response));
    }

    /**
     * Retrieves all ratings received by a specific user with pagination
     */
    @GetMapping("/received/{userId}")
    public ResponseEntity<StandardResponse> getAllReceivedRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse(400, "Invalid user ID", null));
        }

        PaginatedRatingDTO response = ratingService.getAllReceivedRatings(userId, page, size);
        return ResponseEntity.ok(
                new StandardResponse(200, "Retrieved ratings received by user", response));
    }

    /**
     * Retrieves all ratings given by a specific user with pagination
     */
    @GetMapping("/given/{userId}")
    public ResponseEntity<StandardResponse> getAllGivenRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse(400, "Invalid user ID", null));
        }

        PaginatedRatingDTO response = ratingService.getAllGivenRatings(userId, page, size);
        return ResponseEntity.ok(
                new StandardResponse(200, "Retrieved ratings given by user", response));
    }
}