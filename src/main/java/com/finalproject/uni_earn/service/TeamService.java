package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.request.TeamRequestDTO;
import com.finalproject.uni_earn.entity.Team;

public interface TeamService {
    public String createTeam(TeamRequestDTO teamRequest);
    public String addMember(Long teamId, Long studentId);
    public String removeMember(Long teamId, Long studentId);
    public String deleteTeam(Long teamId);
}
