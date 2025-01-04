package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;


@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employers")
public class Employer extends User{

    @Column(name="company_details",nullable = false)
    private String companyDetails;

    @Column(name="location",nullable = false)
    private String location;

    @Column(name = "rating")
    @Min(0)
    @Max(5)
    private float rating;

    @Column(name = "subscribers")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<User> subscribers ;

    @Column(name = "category")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> categories ;

    public Employer() {

    }

}

