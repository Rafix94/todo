package com.todolist.refinementservice.dto;

import java.util.UUID;

public record VotingStatusChangeDTO(
        UUID teamId,
        VotingState votingState
) {}