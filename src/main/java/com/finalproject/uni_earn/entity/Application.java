package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.InvalidValueException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job; // Many applications can apply to one job

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;// Many applications can be made by one student

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(nullable = false)
    private Date appliedDate = new Date();

    public void setSelected(boolean b) {
    }

    // Ensure only one of student or team is set
    @PrePersist
    @PreUpdate
    private void validateApplicant() {
        if ((student == null && team == null) || (student != null && team != null)) {
            throw new InvalidValueException("An application must have either a student OR a team, not both.");
        }
    }
}
