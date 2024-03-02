package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.ErrorDto;
import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.dto.UpdateTaskDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    @GetMapping
    public Page<TaskDto> getTasksForUser(
            @RequestParam String email,
            Pageable pageable,
            @RequestParam(required = false) String searchQuery
    ) {
        return taskService.getTasksForCustomer(email, pageable, searchQuery);
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
        TaskDto taskDto = taskService.getTaskDetails(taskId);
        if (taskDto != null) {
            return ResponseEntity.ok(taskDto);
        } else {
            return ResponseEntity.notFound().build();
        }
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
            @RequestParam String email,
            @Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTaskForUser(email, taskDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
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
        TaskDto updatedTask = taskService.updateTask(taskId, updateTaskDto);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
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
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
