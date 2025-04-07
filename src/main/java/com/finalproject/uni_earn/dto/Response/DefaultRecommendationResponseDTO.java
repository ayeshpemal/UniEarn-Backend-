package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultRecommendationResponseDTO {
    private int status_code;
    private boolean success;
    private String message;
    private Object data;
    private String error;
}
