package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TeamSummaryDto", description = "Object representing a summary of a team with membership status")
public record TeamSummaryDto(
        @Schema(description = "The unique identifier of the team", example = "team-12345", required = true)
        String id,

        @Schema(description = "The name of the team", example = "Engineering Team", required = true)
        String name,

        @Schema(description = "Indicates whether the user is a member of the team", example = "true", required = true)
        boolean isMember
) {}