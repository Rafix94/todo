package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.TeamDetailsDto;
import com.recruitment.useragent.dto.TeamSummaryDto;
import com.recruitment.useragent.model.MembershipStatus;
import com.recruitment.useragent.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final KeycloakUserService keycloakUserService;

    @Autowired
    public TeamsController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping
    public ResponseEntity<List<TeamSummaryDto>> getAllTeamsWithMembershipStatus(

            @RequestParam(value = "membershipStatus", required = false, defaultValue = "ALL") MembershipStatus membershipStatus
    ) {
        List<TeamSummaryDto> groups = keycloakUserService.getAllTeamsWithMembershipStatus(membershipStatus);

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailsDto> getTeamDetails(@PathVariable String teamId) {
        TeamDetailsDto teamDetails = keycloakUserService.getTeamDetails(teamId);
        return ResponseEntity.ok(teamDetails);
    }

    @PostMapping
    public ResponseEntity<TeamDetailsDto> createTeam(@RequestBody TeamDetailsDto teamName) {
        TeamDetailsDto teamDetailsDto = keycloakUserService.createGroup(teamName);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamDetailsDto);
    }

    @PostMapping("/join/{teamId}")
    public ResponseEntity<Void> joinTeam(@PathVariable String teamId) {
        keycloakUserService.joinGroup(teamId);
        return ResponseEntity.noContent().build();
    }
}
