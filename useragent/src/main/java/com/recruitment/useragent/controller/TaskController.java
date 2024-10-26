package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.ErrorDto;
import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.dto.UpdateTaskDto;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.mapper.TaskMapper;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Task management API",
        description = "CRUD operations for tasks"
)
@RestController
@RequestMapping(path="/tasks")
@Validated
@PreAuthorize("hasRole('USER')")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private String getAuthenticatedUserEmail() {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (String) principal.getClaims().get("email");
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
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskDto>> getTasksForUser(
            @RequestParam String email,
            Pageable pageable,
            @RequestParam(required = false) String searchQuery) {
        String authEmail = getAuthenticatedUserEmail();
        if (authEmail != null && authEmail.equals(email)) {
            Page<TaskDto> tasksForCustomer = taskService.getTasksForCustomer(email, pageable, searchQuery);
            return ResponseEntity.ok(tasksForCustomer);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Get task details",
            description = "REST API to fetch details of a single task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "404", description = "HTTP Status Not Found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskDetails(@PathVariable Long taskId) {
        String authEmail = getAuthenticatedUserEmail();
        Task task = taskService.getTaskDetails(taskId);

        if (task != null && task.getCustomer().getEmail().equals(authEmail)) {
            return ResponseEntity.ok(TaskMapper.mapToTaskDto(task));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Create task",
            description = "REST API to create a task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status Created"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTaskForUser(
            @RequestParam String email,
            @Valid @RequestBody TaskDto taskDto) {
        String authEmail = getAuthenticatedUserEmail();
        if (authEmail != null && authEmail.equals(email)) {
            TaskDto createdTask = taskService.createTaskForUser(email, taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Update task",
            description = "REST API to update a task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "404", description = "HTTP Status Not Found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskDto updateTaskDto) {
        String authEmail = getAuthenticatedUserEmail();
        Task task = taskService.getTaskDetails(taskId);
        if (task != null && task.getCustomer().getEmail().equals(authEmail)) {
            TaskDto updatedTask = taskService.updateTask(taskId, updateTaskDto);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Delete task",
            description = "REST API to delete a task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "HTTP Status No Content"),
            @ApiResponse(responseCode = "404", description = "HTTP Status Not Found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        String authEmail = getAuthenticatedUserEmail();
        Task task = taskService.getTaskDetails(taskId);
        if (task != null && task.getCustomer().getEmail().equals(authEmail)) {
            taskService.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
