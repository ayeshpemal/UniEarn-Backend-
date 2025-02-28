package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminResponseDTO {
    private Long userId;
    private String userName;
    private String email;
}
