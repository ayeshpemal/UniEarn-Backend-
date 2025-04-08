package com.finalproject.uni_earn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"teams", "applications", "leadingTeams","followers", "following"})
@Entity
@Table(name = "students")
public class Student extends User {
    @Column(name="display_name", nullable = false)
    private String displayName;

    @Column(name = "university", nullable = false)
    private String university;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    /*
     * @Column(name = "university_location", columnDefinition = "POINT")
     * private Point universityLocation;
     */

    @Column(name = "contact_numbers")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> contactNumbers;

    @Column(name = "rating")
    @Min(0)
    @Max(5)
    private float rating;

    @ElementCollection
    @Column(name = "preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    @Enumerated(EnumType.STRING)
    private List<JobCategory> preferences;

    @Column(name = "skills")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> skills;

    @ToString.Exclude
    @OneToMany(mappedBy = "student")
    private Set<Application> applications;

    @ToString.Exclude
    @OneToMany(mappedBy = "leader")
    private Set<Team> leadingTeams;

    @ToString.Exclude
    @ManyToMany(mappedBy = "members")
    private Set<Team> teams;

    // Add following and followers relationship
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "student_followers",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<Student> followers;

    @ToString.Exclude
    @ManyToMany(mappedBy = "followers")
    private Set<Student> following;

    // Method to follow another student
    public void followStudent(Student student) {
        if (!following.contains(student)) {
            following.add(student);
            student.getFollowers().add(this); // Add current student to the followers of the target student
        }
    }

    // Method to unfollow another student
    public void unfollowStudent(Student student) {
        if (following.contains(student)) {
            following.remove(student);
            student.getFollowers().remove(this); // Remove current student from the followers of the target student
        }
    }

    public void addPreference(JobCategory preference) {
        preferences.add(preference);
    }

    public void clearPreferences() {
        preferences.clear();
    }

    public void addContactNumber(String contactNumber) {
        contactNumbers.add(contactNumber);
    }

    public void clearContactNumbers() {
        contactNumbers.clear();
    }
}
