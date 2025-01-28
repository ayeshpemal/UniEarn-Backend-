package com.finalproject.uni_earn.util.mappers;

import com.finalproject.uni_earn.dto.Response.RatingResponseBasicDTO;
import com.finalproject.uni_earn.entity.Ratings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingResponseBasicDTO entityToDTO(Ratings rating);
}
