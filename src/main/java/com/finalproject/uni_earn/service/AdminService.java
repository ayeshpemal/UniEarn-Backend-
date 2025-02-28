package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Response.AdminResponseDTO;
import com.finalproject.uni_earn.dto.Response.AdminStatsResponseDTO;
import com.finalproject.uni_earn.entity.User;

import java.util.List;

public interface AdminService {
    public String makeUserAdmin(Long userId);
    public String removeAdmin(Long userId);
    public List<AdminResponseDTO> getAllAdmins();
    public AdminStatsResponseDTO getPlatformStatistics();
}
