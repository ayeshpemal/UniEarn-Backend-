package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;

public interface ApplicationService {
    String applyAsStudent(Long studentId, Long jobId);
    String applyAsTeam(Long teamId, Long jobId);
    public  String updateStatus(Long applicationId, ApplicationStatus status);
    public ApplicationDTO viewApplicationDetails(Long applicationId);
    public void deleteApplication(Long applicationId);

}
