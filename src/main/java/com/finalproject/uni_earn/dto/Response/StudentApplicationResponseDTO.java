package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentApplicationResponseDTO {
    private boolean hasApplied;
    private ApplicationDTO application;
    private Boolean memberStatus;
    private Boolean isTeamLeader;
}
