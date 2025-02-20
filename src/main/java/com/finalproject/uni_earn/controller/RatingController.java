package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Paginated.PaginatedRatingDTO;
import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.dto.request.ReatingRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateRatingRequestDTO;
import com.finalproject.uni_earn.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/rating")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Test endpoint for creating a new rating
     * Sample request: POST /api/v1/rating/test/create
     */
    @PostMapping("/test/create")
    public ResponseEntity<RatingResponseBasicDTO> testCreateRating() {
        // Create a test rating request
        ReatingRequestDTO testRating = new ReatingRequestDTO(
                5L,  // Student (rater)
                11L, // Employer (rated)
                1L,  // Job ID
                5,   // Score
                "Excellent work environment and great learning experience"
        );

        return ResponseEntity.ok(ratingService.saveRating(testRating));
    }

    /**
     * Test endpoint for updating an existing rating
     * Sample request: PUT /api/v1/rating/test/update
     */
    @PutMapping("/test/update")
    public ResponseEntity<RatingResponseBasicDTO> testUpdateRating() {
        // Create a test update rating request
        UpdateRatingRequestDTO updateRequest = new UpdateRatingRequestDTO(
                2L,  // Rating ID
                2L,  // Student (rater)
                11L, // Employer (rated)
                1L,  // Job ID
                4,   // New score
                "Good experience overall, but could improve communication"
        );

        return ResponseEntity.ok(ratingService.updateRating(updateRequest));
    }

    /**
     * Test endpoint for deleting a rating
     * Sample request: DELETE /api/v1/rating/test/delete
     */
    @DeleteMapping("/test/delete")
    public ResponseEntity<RatingResponseBasicDTO> testDeleteRating() {
        // Create a test delete rating request
        UpdateRatingRequestDTO deleteRequest = new UpdateRatingRequestDTO(
                1L,  // Rating ID
                1L,  // Student (rater)
                11L, // Employer (rated)
                1L,  // Job ID
                null, // Score not needed for delete
                null  // Comment not needed for delete
        );

        return ResponseEntity.ok(ratingService.deleteRating(deleteRequest));
    }

    /**
     * Test endpoint for getting all received ratings for a user
     * Sample request: GET /api/v1/rating/test/received/11?page=0&size=10
     */
    @GetMapping("/test/received/{userId}")
    public ResponseEntity<PaginatedRatingDTO> testGetReceivedRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ratingService.getAllReceivedRatings(userId, page, size));
    }

    /**
     * Test endpoint for getting all given ratings by a user
     * Sample request: GET /api/v1/rating/test/given/1?page=0&size=10
     */
    @GetMapping("/test/given/{userId}")
    public ResponseEntity<PaginatedRatingDTO> testGetGivenRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ratingService.getAllGivenRatings(userId, page, size));
    }

    // Regular endpoints for production use would go here
}