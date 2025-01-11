package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.Job;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private int id;
    private String title;
    private String description;
    private String location;
    private String category;
    private double payment;
    private Date startDate;
    private Date endDate;
    //private Employer postedBy;
    @Enumerated(EnumType.STRING) // Store enum as a string in the database
    private Job.Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
