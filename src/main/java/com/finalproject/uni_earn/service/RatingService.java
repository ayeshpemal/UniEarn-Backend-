package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedRatingDTO;
import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.dto.request.ReatingRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdateRatingRequestDTO;


public interface RatingService {

    RatingResponseBasicDTO saveRating(ReatingRequestDTO reatingRequestDTO);
    RatingResponseBasicDTO updateRating(UpdateRatingRequestDTO updateRatingRequestDTO);
    RatingResponseBasicDTO deleteRating(UpdateRatingRequestDTO updateRatingRequestDTO);
    PaginatedRatingDTO getAllReceivedRatings (long UserId, int page, int size);
    PaginatedRatingDTO getAllGivenRatings (long UserId,int page,int size);
    Double getUserAverageRating(long userId);
}
