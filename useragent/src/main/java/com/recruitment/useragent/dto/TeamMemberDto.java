package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TeamMemberDto", description = "Object representing a member of a team")
public record TeamMemberDto(
        @Schema(description = "The unique identifier of the team member", example = "member-12345", required = true)
        String id,

        @Schema(description = "The username of the team member", example = "johndoe", required = true)
        String userName,

        @Schema(description = "The email address of the team member", example = "johndoe@example.com", required = true)
        String email
) {}