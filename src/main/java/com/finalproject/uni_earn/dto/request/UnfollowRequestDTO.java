package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UnfollowRequestDTO {
    private Long studentId;
    private Long employerId;
}