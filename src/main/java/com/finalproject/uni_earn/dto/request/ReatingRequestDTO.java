package com.finalproject.uni_earn.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReatingRequestDTO {

    private Long raterId;
    private Long ratedId;
    private Long applicationId;
    private Integer score;
    private String comment;

}
