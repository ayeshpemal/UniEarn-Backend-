package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.Gender;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "students")
public class Student extends User {
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

    @OneToMany(mappedBy = "student")
    private Set<Application> orderDetails;

    public void addPreference(JobCategory preference) {
        preferences.add(preference);
    }

    public void clearPreferences() {
        preferences.clear();
    }
}
