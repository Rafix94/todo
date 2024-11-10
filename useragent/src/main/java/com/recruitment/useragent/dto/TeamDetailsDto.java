package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "TeamDetailsDto", description = "Object representing detailed information of a team")
public record TeamDetailsDto(
        @Schema(description = "The unique identifier of the team", example = "team-12345", required = true)
        String id,

        @Schema(description = "The name of the team", example = "Development Team", required = true)
        String name,

        @Schema(description = "List of team members", required = true)
        List<TeamMemberDto> members
) {}