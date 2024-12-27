package com.todolist.refinementservice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record SessionState (
        Map<UUID, UserVoteState> participantsVotes,
        UUID adminId,
        VotingState votingState,
        Task selectedTask
) {
    public static SessionState newSession() {
        return new SessionState(new HashMap<>(), null, VotingState.IDLE, null);
    }
}
