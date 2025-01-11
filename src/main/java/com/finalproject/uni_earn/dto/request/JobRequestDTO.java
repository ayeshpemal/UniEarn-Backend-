package com.finalproject.uni_earn.dto.request;

import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.enums.JobCategory;
import com.finalproject.uni_earn.entity.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestDTO {

    private String jobTitle;
    private JobCategory jobCategory;
    private String jobDescription;
    private Location jobLocation;
    private double jobPayment;
    private Date startDate;
    private Date endDate;
    private Employer employer;
    private boolean status;
}
