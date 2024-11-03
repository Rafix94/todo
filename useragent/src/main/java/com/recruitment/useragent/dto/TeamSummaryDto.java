package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "MembershipDto",
        description = "Object representing a team with membership information"
)
public record TeamSummaryDto(
        String id,
        String name,
        boolean isMember
) {}
