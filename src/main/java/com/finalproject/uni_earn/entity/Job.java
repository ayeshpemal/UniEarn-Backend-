package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="jobs")
public class Job {
    @Id
    @Column(name="job_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobId;

    @Column(name="job_title",length = 50, nullable = false)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_category", length = 50, nullable = false)
    private JobCategory jobCategory;

    @Column(name="job_description",length = 255)
    private String jobDescription;

    @Column(name="job_locations", nullable = false)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Location> jobLocations;
    //If job has many locations....

    @Column(name = "job_payment", nullable = false)
    private double jobPayment;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Column(name = "active_status", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private boolean activeStatus;


   
}
