package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TeamMemberDto",
        description = "Object representing a team member"
)
public record TeamMemberDto(
        String id,
        String userName,
        String email
) {}
