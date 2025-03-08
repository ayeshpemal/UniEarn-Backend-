package com.finalproject.uni_earn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDTO {
    private Long userId;
    private String userName;
    private String displayName;
    private String university;
    private String profilePicture;
    private boolean isFollow;
}
