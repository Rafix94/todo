package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.ErrorDto;
import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.mapper.TaskMapper;
import com.recruitment.useragent.repository.TaskRepository;
import com.recruitment.useragent.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "Task management API",
        description = "CRUD operation for task")
@RestController
@RequestMapping(path="/tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Get tasks",
            description = "REST API to fetch tasks"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping("/{userId}")
    public Page<TaskDto> getTasksForUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return taskService.getTasksForUser(userId, pageable);
    }

    @Operation(
            summary = "Create tasks",
            description = "REST API to create tasks"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status Created"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping("/{customerId}")
    public ResponseEntity<TaskDto> createTaskForUser(
            @PathVariable Long customerId,
            @Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTaskForUser(customerId, taskDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }
}
