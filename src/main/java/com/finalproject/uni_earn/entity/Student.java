package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.Gender;
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

    @Column(name = "home_address")
    private String homeAddress;

    /*@Column(name = "university_location", columnDefinition = "POINT")
    private Point universityLocation;*/

    @Column(name = "contact_numbers")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> contactNumbers;

    @Column(name = "rating")
    @Min(0)
    @Max(5)
    private float rating;

    @Column(name = "preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> preferences;

    @Column(name = "skills")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> skills;
}
