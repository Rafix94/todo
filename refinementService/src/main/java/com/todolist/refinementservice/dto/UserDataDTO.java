package com.todolist.refinementservice.dto;

import java.util.UUID;

public record UserDataDTO(
        UUID id,
        String firstName,
        String lastName
) {}
