package com.todolist.refinementservice.dto;

import com.todolist.refinementservice.model.VotingState;

import java.util.UUID;

public record VotingStatusChangeDTO(
        UUID teamId,
        VotingState votingState
) {}