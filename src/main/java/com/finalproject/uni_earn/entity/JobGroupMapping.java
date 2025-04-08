package com.finalproject.uni_earn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_group_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobGroupMapping extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_job_id", nullable = false)
    private String groupJobId; // Unique ID for the group

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
