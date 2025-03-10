package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.ReportState;
import com.finalproject.uni_earn.entity.enums.ReportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reports")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reports extends Auditable {

    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "feedback", length = 2000 , nullable = false)
    private String feedback;

    @CreationTimestamp
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportState status;

    @Column(name = "penalty_score" , nullable = false)
    private double penaltyScore;

}
