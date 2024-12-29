package com.todolist.refinementservice.dto;

import java.util.UUID;

public record UserPresenceActionDTO(
        UUID userId,
        UUID teamId,
        PresenceAction presenceAction
)
{}
