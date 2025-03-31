package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupMemberDTO {
    private Long id;
    private String name;
    private Location location;
    private float rating;
    private String profilePictureUrl;
    private boolean isRated;

}

