package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.UserRatingDetailsResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedRatingDTO {
    private List<UserRatingDetailsResponseDTO> RatingDetails;
    private long ratingCount;
}
