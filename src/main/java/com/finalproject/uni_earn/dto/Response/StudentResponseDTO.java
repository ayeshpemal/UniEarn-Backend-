package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentResponseDTO {
    private long userId;
    private String userName;
    private String displayName;
    private Gender gender;
    private float rating;
}
