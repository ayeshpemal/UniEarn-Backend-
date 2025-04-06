package com.finalproject.uni_earn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(exclude = {"leader", "members", "applications", "memberConfirmations"})
@Table(name = "team")
public class Team extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String teamName;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    private Student leader;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> members = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "confirmed")
    private Map<Student, Boolean> memberConfirmations = new HashMap<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "team")
    private Set<Application> applications;

    public void addMember(Student student) {
        //this.members.add(student);
        this.memberConfirmations.put(student, false); // Default confirmation status is false
    }
}
