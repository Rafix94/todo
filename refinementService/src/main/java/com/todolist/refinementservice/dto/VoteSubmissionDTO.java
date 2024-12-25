package com.todolist.refinementservice.dto;

import java.util.UUID;

public record VoteSubmissionDTO(
        UUID teamId,
        UUID userId,
        int vote
) {}