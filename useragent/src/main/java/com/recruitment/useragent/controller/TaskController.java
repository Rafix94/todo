package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.mapper.TaskMapper;
import com.recruitment.useragent.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskRepository.findAll().stream().map(TaskMapper::mapToTaskDto).collect(Collectors.toList()));
    }
}
