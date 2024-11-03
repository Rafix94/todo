package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "TeamDto",
        description = "Object representing a team"
)
public record TeamDetailsDto(
        String id,
        String name,
        List<TeamMemberDto> members
) {}
