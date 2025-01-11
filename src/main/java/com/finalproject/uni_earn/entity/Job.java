package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="jobs")
public class Job {
    @Id
    @Column(name="job_id",length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int jobId;

    @Column(name="job_title",length = 50, nullable = false)
    private String jobTitle;

    @Column(name = "job_category", length = 50, nullable = false)
    private JobCategory jobCategory;

    @Column(name="job_description",length = 255)
    private String jobDescription;

    @Column(name="job_location", nullable = false)
    private Location jobLocation;

    @Column(name = "job_payment", nullable = false)
    private double jobPayment;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "employer", nullable = false)
    private Employer employer;

    @Column(name = "job_status", columnDefinition = "TINYINT default 0", nullable = false)
    private boolean jobStatus;

}
