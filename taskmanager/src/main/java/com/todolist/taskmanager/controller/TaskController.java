package com.todolist.taskmanager.controller;

import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.dto.ErrorDto;
import com.todolist.taskmanager.dto.UpdateTaskDto;
import com.todolist.taskmanager.service.TaskService;
import com.todolist.taskmanager.dto.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Task Management API",
        description = "CRUD operations for managing tasks"
)
@RestController
@RequestMapping(path = "/tasks")
@Validated
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks created by the user",
            description = "Retrieve a paginated list of tasks created by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getTasksCreatedByUser(Pageable pageable) {
        Page<TaskDto> tasksCreatedByUser = taskService.getTasksCreatedByUser(pageable);
        return ResponseEntity.ok(tasksCreatedByUser);
    }

    @Operation(
            summary = "Update a specific task",
            description = "Update the details of a task, only if the authenticated user is the task owner"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}")
    @PreAuthorize("@taskSecurityService.isOwner(#taskId)")
    public ResponseEntity<TaskDto> updateTask(@PathVariable long taskId, @RequestBody @Valid UpdateTaskDto updateTaskDto) {
        TaskDto taskDto = taskService.updateTask(taskId, updateTaskDto);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(
            summary = "Create a new task",
            description = "Create a new task and assign it to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateTaskDto createTaskDto) {
        TaskDto taskDto = taskService.createTask(createTaskDto);
        return ResponseEntity.status(201).body(taskDto);
    }

    @Operation(
            summary = "Get task details by ID",
            description = "Retrieve details of a specific task if the authenticated user is the task owner"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping("/{taskId}")
    @PreAuthorize("@taskSecurityService.isOwner(#taskId)")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable long taskId) {
        TaskDto taskDto = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(
            summary = "Delete a specific task",
            description = "Delete a task if the authenticated user is the task owner"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @DeleteMapping("/{taskId}")
    @PreAuthorize("@taskSecurityService.isOwner(#taskId)")
    public ResponseEntity<Void> deleteTaskById(@PathVariable long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();  // No content response for deletion
    }

    @Operation(
            summary = "Assign a task to a user",
            description = "This endpoint allows assigning a task identified by the taskId to the currently authenticated user. It updates the task's assigned user and returns the updated task details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully assigned", content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}/assign")
    @PreAuthorize("@taskSecurityService.isOwner(#taskId)")
    public ResponseEntity<TaskDto> assignTask(@PathVariable Long taskId) {
        TaskDto taskDto = taskService.assignTaskToUser(taskId);
        return ResponseEntity.ok(taskDto);
    }
}

