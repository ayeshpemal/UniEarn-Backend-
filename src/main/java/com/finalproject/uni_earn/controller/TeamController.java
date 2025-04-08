package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.TeamDTO;
import com.finalproject.uni_earn.dto.request.TeamRequestDTO;
import com.finalproject.uni_earn.entity.Team;
import com.finalproject.uni_earn.service.TeamService;
import com.finalproject.uni_earn.util.StandardResponse;
import jdk.jfr.Percentage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createTeam(@RequestBody TeamRequestDTO teamRequest) {
        Long teamId = teamService.createTeam(teamRequest);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success", teamId),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/{teamId}")
    public ResponseEntity<StandardResponse> getTeam(@PathVariable Long teamId) {
        TeamDTO team = teamService.getTeam(teamId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", team),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{teamId}/add-member/{studentId}")
    public ResponseEntity<StandardResponse> addMember(@PathVariable Long teamId, @PathVariable Long studentId) {
        String message = teamService.addMember(teamId, studentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{teamId}/remove-member/{studentId}")
    public ResponseEntity<StandardResponse> removeMember(@PathVariable Long teamId, @PathVariable Long studentId) {
        String message = teamService.removeMember(teamId, studentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @DeleteMapping("/{teamId}")
    public ResponseEntity<StandardResponse> deleteTeam(@PathVariable Long teamId) {
        String message = teamService.deleteTeam(teamId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @PutMapping("/{studentId}/confirm/{teamId}")
    public ResponseEntity<StandardResponse> confirmMembership(
            @PathVariable Long teamId,
            @PathVariable Long studentId) {

        teamService.confirmApplication(teamId, studentId);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", "Member confirmation updated."),
                HttpStatus.OK
        );
    }
}
