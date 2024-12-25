package com.todolist.refinementservice.dto;

import java.util.Map;
import java.util.UUID;

public record VotingStatusDTO(
        UUID teamId,
        boolean active,
        Map<UUID, Integer> votes
)
{}
