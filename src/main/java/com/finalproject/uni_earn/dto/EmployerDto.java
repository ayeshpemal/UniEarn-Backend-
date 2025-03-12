package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployerDto {
    private Long userId;
    private String userName;
    private String companyName;
    private String companyDetails;
    private String location;
    private List<String> categories ;
    private boolean follow;
}
