package com.todolist.taskmanager.controller;

import com.todolist.ScanningStatus;

public record FileDto(
        Long id,
        String name,
        Long size,
        String url,
        ScanningStatus scanningStatus
) {}
