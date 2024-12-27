package com.todolist.refinementservice.dto;

import com.todolist.refinementservice.model.Task;
import com.todolist.refinementservice.model.VotingState;

import java.util.Map;
import java.util.UUID;

public record SessionStateDTO(
        Map<UserDataDTO, UserVoteStateDTO> participantsVotes,
        UUID adminId,
        VotingState votingState,
        Task task
) {}
