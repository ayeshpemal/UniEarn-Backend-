package com.finalproject.uni_earn.dto.request;

import com.finalproject.uni_earn.dto.LocationDTO;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddJobRequestDTO {

    private String jobTitle;
    private JobCategory jobCategory;
    private String jobDescription;
    private List<LocationDTO> jobLocations;
    private LocalTime startTime;
    private LocalTime endTime;
    private double jobPayment;
    private int requiredWorkers;
    private List<Gender> requiredGender;
    private Long employer;
}
