package com.todolist.refinementservice.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionState {
    private final Map<UUID, UserVoteState> participantsVotes = new HashMap<>();
    private UUID adminId = null;
    private VotingState votingState = VotingState.IDLE;
    private Task selectedTask;
}
