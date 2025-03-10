package com.finalproject.uni_earn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.JobStatus;
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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="jobs")
public class Job extends Auditable {
    @Id
    @Column(name="job_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(name="job_title",length = 50, nullable = false)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_category", length = 50, nullable = false)
    private JobCategory jobCategory;

    @Column(name="job_description",length = 255)
    private String jobDescription;

//    @Enumerated(EnumType.STRING)
//    @ElementCollection
//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(name="job_locations", nullable = false)
//    private List<Location> jobLocations;
//    //If job has many locations....

    @Column(name = "job_locations",nullable = false)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Location> jobLocations;

    @Column(name = "job_payment", nullable = false)
    private double jobPayment;

    @Column(name = "required_workers", nullable = false)
    private int requiredWorkers;

    @Column(name = "required_gender", nullable = false)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Gender> requiredGender;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    // ✅ Add the One-To-Many Relationship with Applications
    @JsonIgnore
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Application> applications;

    @Column(name = "active_status", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private boolean activeStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    private JobStatus jobStatus = JobStatus.PENDING;
   
}
