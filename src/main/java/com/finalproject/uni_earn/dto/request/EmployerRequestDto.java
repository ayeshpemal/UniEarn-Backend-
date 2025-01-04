package com.finalproject.uni_earn.dto.request;

import com.finalproject.uni_earn.entity.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

public class EmployerRequestDto {

    private String companyDetails;
    private String location;
    private float rating;
    private List<User> subscribers ;
    private List<String> categories ;
}
