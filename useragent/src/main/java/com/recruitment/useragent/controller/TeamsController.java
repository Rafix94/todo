package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.TeamDetailsDto;
import com.recruitment.useragent.dto.TeamSummaryDto;
import com.recruitment.useragent.model.MembershipStatus;
import com.recruitment.useragent.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Team Management API", description = "APIs for managing teams and memberships")
@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final KeycloakUserService keycloakUserService;

    @Autowired
    public TeamsController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @Operation(summary = "Retrieve all teams with specified membership status", description = "Returns a list of teams filtered by membership status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teams retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    public ResponseEntity<List<TeamSummaryDto>> getAllTeamsWithMembershipStatus(
            @RequestParam(value = "membershipStatus", required = false, defaultValue = "ALL") MembershipStatus membershipStatus) {
        List<TeamSummaryDto> groups = keycloakUserService.getAllTeamsWithMembershipStatus(membershipStatus);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Get details of a specific team", description = "Retrieve detailed information for a specified team by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailsDto> getTeamDetails(@PathVariable String teamId) {
        TeamDetailsDto teamDetails = keycloakUserService.getTeamDetails(teamId);
        return ResponseEntity.ok(teamDetails);
    }

    @Operation(summary = "Create a new team", description = "Create a new team with the specified name")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid team data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<TeamDetailsDto> createTeam(@RequestBody TeamDetailsDto teamName) {
        TeamDetailsDto teamDetailsDto = keycloakUserService.createGroup(teamName);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamDetailsDto);
    }

    @Operation(summary = "Join a specific team", description = "Allows the user to join a team specified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully joined the team"),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/join/{teamId}")
    public ResponseEntity<Void> joinTeam(@PathVariable String teamId) {
        keycloakUserService.joinGroup(teamId);
        return ResponseEntity.noContent().build();
    }
}