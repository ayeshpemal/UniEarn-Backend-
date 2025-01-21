package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
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
    @JoinColumn(name = "student_id")
    private User student; // Many applications can be made by one student


    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;


    private Date appliedDate;

    public void setSelected(boolean b) {
    }
}
