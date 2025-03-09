package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private Long jobId;
    private String jobTitle;
    private JobCategory jobCategory;
    private String jobDescription;
    private List<Location> jobLocations;
    private double jobPayment;
    private int requiredWorkers;
    private Gender requiredGender;
    private Date startDate;
    private Date endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Employer employer;
    private boolean activeStatus;

}
