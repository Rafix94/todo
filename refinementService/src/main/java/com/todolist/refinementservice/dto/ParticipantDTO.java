package com.todolist.refinementservice.dto;

import java.util.UUID;

public record ParticipantDTO (
        UUID userId,
        String mail,
        String firstName,
        String lastName
)
{}
