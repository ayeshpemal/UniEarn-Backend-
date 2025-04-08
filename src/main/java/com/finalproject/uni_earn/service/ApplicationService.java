package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.ApplicationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedGroupApplicationDTO;
import com.finalproject.uni_earn.dto.Paginated.PaginatedStudentApplicationDTO;
import com.finalproject.uni_earn.dto.Response.GroupApplicationDTO;
import com.finalproject.uni_earn.dto.Response.StudentApplicationDTO;
import com.finalproject.uni_earn.dto.Response.StudentApplicationResponseDTO;
import com.finalproject.uni_earn.dto.request.StudentSummaryRequestDTO;
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

    Map<String, Object> getStudentApplicationsSummary(StudentSummaryRequestDTO requestDTO);

    List<GroupApplicationDTO> getGroupApplicationsByJobId(Long jobId);

    List<StudentApplicationDTO> getPendingStudentsByJobId(Long jobId);

    //List<StudentApplicationDTO> getPendingStudentsByJobId(Long jobId);
    StudentApplicationResponseDTO hasStudentAppliedForJob(Long studentId, Long jobId);

    PaginatedGroupApplicationDTO getPaginatedGroupApplicationsByJobId(Long jobId, int page, int pageSize);

    PaginatedStudentApplicationDTO getPaginatedStudentApplicationsByJobId(Long jobId, int page, int pageSize);
}
