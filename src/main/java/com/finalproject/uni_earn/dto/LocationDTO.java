package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Location location;
    private Date startDate;
    private Date endDate;
}
