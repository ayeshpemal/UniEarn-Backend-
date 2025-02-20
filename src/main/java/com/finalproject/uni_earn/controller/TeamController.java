package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.request.TeamRequestDTO;
import com.finalproject.uni_earn.entity.Team;
import com.finalproject.uni_earn.service.TeamService;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createTeam(@RequestBody TeamRequestDTO teamRequest) {
        String message = teamService.createTeam(teamRequest);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(201, "Success", message),
                HttpStatus.CREATED);
    }

    @PostMapping("/{teamId}/add-member/{studentId}")
    public ResponseEntity<StandardResponse> addMember(@PathVariable Long teamId, @PathVariable Long studentId) {
        String message = teamService.addMember(teamId, studentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}/remove-member/{studentId}")
    public ResponseEntity<StandardResponse> removeMember(@PathVariable Long teamId, @PathVariable Long studentId) {
        String message = teamService.removeMember(teamId, studentId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<StandardResponse> deleteTeam(@PathVariable Long teamId) {
        String message = teamService.deleteTeam(teamId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK);
    }
}
