package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.Response.GroupApplicationDTO;
import com.finalproject.uni_earn.dto.Response.StudentApplicationDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;

import java.util.List;
import java.util.Map;

public interface ApplicationService {
    String applyAsStudent(Long studentId, Long jobId);
    String applyAsTeam(Long teamId, Long jobId);
    void updateStatus(Long applicationId, ApplicationStatus newStatus, User user);
    public ApplicationDTO viewApplicationDetails(Long applicationId);
    public void deleteApplication(Long applicationId);

    Map<String, Object> getStudentApplicationsSummary(Long studentId);

    List<GroupApplicationDTO> getGroupApplicationsByJobId(Long jobId);

    List<StudentApplicationDTO> getPendingStudentsByJobId(Long jobId);

    //List<StudentApplicationDTO> getPendingStudentsByJobId(Long jobId);
}
