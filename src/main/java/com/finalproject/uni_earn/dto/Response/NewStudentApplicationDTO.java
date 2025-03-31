package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewStudentApplicationDTO {
    private Long applicationId;
    private Long id;
    private String name;
    private Location location;
    private float rating;
    private ApplicationStatus applicationStatus;
    private String profilePictureUrl;
    private boolean isRated;
}
