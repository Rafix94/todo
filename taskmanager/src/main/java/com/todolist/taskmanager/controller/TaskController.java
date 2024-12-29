package com.todolist.taskmanager.controller;

import com.todolist.taskmanager.dto.*;
import com.todolist.taskmanager.model.File;
import com.todolist.taskmanager.service.FileService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Task Management API", description = "CRUD operations for managing tasks by team")
@RestController
@RequestMapping(path = "/tasks")
@Validated
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Get all tasks for a specific team", description = "Retrieve a paginated list of tasks for a team the user belongs to, specified by teamId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access this team's tasks"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping
    @PreAuthorize("@teamSecurityService.userBelongsToTeam(#teamId)")
    public ResponseEntity<Page<TaskDto>> getTasksByTeam(@RequestParam UUID teamId, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByTeam(teamId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get all tasks for a specific team", description = "Retrieve a paginated list of tasks for a team the user belongs to, specified by teamId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access this team's tasks"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("@teamSecurityService.userBelongsToTeam(#teamId)")
    public ResponseEntity<List<TaskDto>> getAllTasksByTeam(@RequestParam UUID teamId, Pageable pageable) {
        List<TaskDto> tasks = taskService.getAllTasksByTeam(teamId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Create a new task for a specific team", description = "Create a new task for a team that the authenticated user is a member of.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create tasks in this team"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping
    @PreAuthorize("@teamSecurityService.userBelongsToTeam(#createTaskDto.teamId)")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateTaskDto createTaskDto) {
        TaskDto taskDto = taskService.createTask(createTaskDto);
        return ResponseEntity.status(201).body(taskDto);
    }

    @Operation(summary = "Update a specific task", description = "Update a task for a team that the authenticated user is a member of.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<TaskDto> updateTask(@PathVariable long taskId, @RequestBody @Valid UpdateTaskDto updateTaskDto) {
        TaskDto taskDto = taskService.updateTask(taskId, updateTaskDto);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(summary = "Delete a specific task", description = "Delete a task if the authenticated user is a member of the associated team.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @DeleteMapping("/{taskId}")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<Void> deleteTaskById(@PathVariable long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign a specific task to a user", description = "Assign a user to a task if the authenticated user has permission within the associated team.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully assigned"),
            @ApiResponse(responseCode = "403", description = "User not authorized to assign this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}/assign")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<TaskDto> assignTask(@PathVariable long taskId) {
        TaskDto updatedTask = taskService.assignTask(taskId);
        return ResponseEntity.ok(updatedTask);
    }


    @Operation(summary = "Update task weight", description = "Update the weight of a specific task if the authenticated user has permission within the associated team.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task weight successfully updated"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this task"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PutMapping("/{taskId}/weight")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<TaskDto> updateWeight(@PathVariable long taskId, @RequestBody WeightDto weightDto) {
        TaskDto updatedTask = taskService.updateWeight(taskId, weightDto);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{taskId}/details")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<TaskDto> getTaskDetails(@PathVariable long taskId) {
        TaskDto taskDto = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping("/{taskId}/comments")
    @PreAuthorize("@teamSecurityService.taskBelongsToUsersTeam(#taskId)")
    public ResponseEntity<TaskDto> uploadFile(
            @RequestParam(value = "file", required = false) MultipartFile multipartFile,
            @RequestParam(value = "comment", required = false) String commentValue,
            @PathVariable("taskId") Long taskId) {

        TaskDto taskDto = taskService.addCommentToTask(taskId, multipartFile, commentValue);

        return ResponseEntity.ok(taskDto);
    }
}