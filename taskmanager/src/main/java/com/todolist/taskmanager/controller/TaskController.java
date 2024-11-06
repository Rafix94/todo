package com.todolist.taskmanager.controller;

import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.dto.ErrorDto;
import com.todolist.taskmanager.dto.UpdateTaskDto;
import com.todolist.taskmanager.dto.TaskDto;
import com.todolist.taskmanager.service.TaskService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Task Management API",
        description = "CRUD operations for managing tasks by team"
)
@RestController
@RequestMapping(path = "/tasks")
@Validated
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks for a specific team",
            description = "Retrieve a paginated list of tasks for a team the user belongs to, specified by teamId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access this team's tasks"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping
//    @PreAuthorize("@teamSecurityService.userBelongsToTeam(#teamId)")
    public ResponseEntity<Page<TaskDto>> getTasksByTeam(
            @RequestParam UUID teamId,
            Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByTeam(teamId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Create a new task for a specific team",
            description = "Create a new task for a team that the authenticated user is a member of."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create tasks in this team"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping
//    @PreAuthorize("@teamSecurityService.userBelongsToTeam(#createTaskDto.teamId)")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateTaskDto createTaskDto) {
        TaskDto taskDto = taskService.createTask(createTaskDto);
        return ResponseEntity.status(201).body(taskDto);
    }

    @Operation(
            summary = "Update a specific task",
            description = "Update a task for a team that the authenticated user is a member of."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}")
//    @PreAuthorize("@taskSecurityService.isOwnerAndInTeam(#taskId)")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable long taskId,
            @RequestBody @Valid UpdateTaskDto updateTaskDto) {
        TaskDto taskDto = taskService.updateTask(taskId, updateTaskDto);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(
            summary = "Delete a specific task",
            description = "Delete a task if the authenticated user is a member of the associated team."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @DeleteMapping("/{taskId}")
//    @PreAuthorize("@teamSecurityService.isOwnerAndInTeam(#taskId)")
    public ResponseEntity<Void> deleteTaskById(@PathVariable long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Assign a specific task to a user",
            description = "Assign a user to a task if the authenticated user has permission within the associated team."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully assigned"),
            @ApiResponse(responseCode = "403", description = "User not authorized to assign this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class))
            )
    })
    @PutMapping("/{taskId}/assign")
// @PreAuthorize("@teamSecurityService.isOwnerAndInTeam(#taskId)")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable long taskId) {
        TaskDto updatedTask = taskService.assignTask(taskId);
        return ResponseEntity.ok(updatedTask);
    }
}