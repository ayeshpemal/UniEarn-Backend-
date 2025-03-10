package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.TeamDTO;
import com.finalproject.uni_earn.dto.request.TeamRequestDTO;
import com.finalproject.uni_earn.entity.Application;
import com.finalproject.uni_earn.entity.Student;
import com.finalproject.uni_earn.entity.Team;
import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import com.finalproject.uni_earn.exception.AlreadyExistException;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.repo.ApplicationRepo;
import com.finalproject.uni_earn.repo.StudentRepo;
import com.finalproject.uni_earn.repo.TeamRepo;
import com.finalproject.uni_earn.service.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamServiceIMPL implements TeamService {

    @Autowired
    private TeamRepo teamRepository;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private ApplicationRepo applicationRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Transactional
    public Long createTeam(TeamRequestDTO teamRequest) {
        Student leader = studentRepository.findById(teamRequest.getLeader())
                .orElseThrow(() -> new NotFoundException("Leader not found"));

        Team team = new Team();
        team.setTeamName(teamRequest.getTeamName());
        team.setLeader(leader);
        team.addMember(leader); // Leader is also a team member
        teamRepository.save(team);
        team.getMemberConfirmations().put(leader,true);// Confirm the leader's membership

        return team.getId();
    }

    @Transactional
    public String addMember(Long teamId, Long studentId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (teamRepository.isStudentInTeam(teamId, studentId)) {
            throw new AlreadyExistException("Student is already a member of this team.");
        }

        team.addMember(student);
        teamRepository.save(team);
        return student.getUserName() + " added to team " + team.getTeamName();
    }

    @Transactional
    public String removeMember(Long teamId, Long studentId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        if (!teamRepository.isStudentInTeam(teamId, studentId)) {
            throw new NotFoundException("Student is not a member of this team.");
        }
        if(team.getLeader().equals(student)){
            throw new InvalidValueException("Leader cannot be removed from the team");
        }

        teamRepository.removeStudentFromTeam(teamId, studentId);
        return student.getUserName() + " removed from team " + team.getTeamName();
    }

    @Transactional
    public String deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));

        teamRepository.removeAllMembersFromTeam(teamId);
        teamRepository.deleteById(teamId);
        return "Team deleted successfully with ID: " + teamId;
    }

    @Override
    public TeamDTO getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        return modelMapper.map(team, TeamDTO.class);
    }

    public void confirmApplication(Long teamId, Long studentId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("Team not found"));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

        if (!team.getMemberConfirmations().containsKey(student)) {
            throw new NotFoundException("Student is not a team member.");
        }

        if (team.getMemberConfirmations().get(student)) {
            throw new AlreadyExistException("Student has already confirmed membership.");
        }

        team.getMemberConfirmations().put(student, true);
        teamRepository.save(team);

        // Check if all members confirmed
        if (team.getMemberConfirmations().values().stream().allMatch(Boolean::booleanValue)) {
            Application application = applicationRepository.findByTeamAndStatus(team, ApplicationStatus.INACTIVE)
                    .orElseThrow(() -> new NotFoundException("No inactive application found"));
            application.setStatus(ApplicationStatus.PENDING);
            applicationRepository.save(application);
        }
    }

}
