package com.finalproject.uni_earn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Job {
    @Id
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
    private Status status;

    public int getJobId() {
        return this.id;
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
