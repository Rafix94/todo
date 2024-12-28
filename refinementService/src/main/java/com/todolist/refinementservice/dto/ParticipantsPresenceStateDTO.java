package com.todolist.refinementservice.dto;

import com.todolist.refinementservice.model.Task;
import com.todolist.refinementservice.model.VotingState;

import java.util.Map;
import java.util.UUID;

public record ParticipantsPresenceStateDTO(
        Map<UserDataDTO, UserVoteStateDTO> participantsVotes
) {}
