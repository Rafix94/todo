package com.todolist.taskmanager.service;

import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.dto.UpdateTaskDto;
import com.todolist.taskmanager.exception.NotFoundException;
import com.todolist.taskmanager.mapper.TaskMapper;
import com.todolist.taskmanager.model.Comment;
import com.todolist.taskmanager.model.Task;
import com.todolist.taskmanager.repository.TaskRepository;
import com.todolist.taskmanager.dto.TaskDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final KeycloakUserService keycloakUserService;
    private final FileService fileService;
    private final CommentService commentService;
    private final TaskMapper taskMapper;

    public Page<TaskDto> getTasksByTeam(UUID teamId, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        Page<Task> tasks = taskRepository.findByTeamId(teamId, pageable);

        return tasks.map(this::getTaskDto);
    }

    public TaskDto createTask(CreateTaskDto createTaskDto) {
        Task task = taskRepository.save(taskMapper.mapToTask(createTaskDto));

        return getTaskDto(task);
    }

    public TaskDto getTaskById(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        return getTaskDto(task);
    }

    public TaskDto addCommentToTask(long taskId, MultipartFile multipartFile, String commentValue) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        Comment comment = commentService.createComment(commentValue, task);

        fileService.createFile(multipartFile, comment);

        return getTaskDto(task);
    }

    @NotNull
    private TaskDto getTaskDto(Task task) {
        String creatorEmail = keycloakUserService.getUserById(task.getCreatedBy().toString())
                .map(UserRepresentation::getEmail)
                .orElse("Unknown");

        String assigneeEmail = task.getAssignedTo() != null
                ? keycloakUserService.getUserById(task.getAssignedTo().toString())
                .map(UserRepresentation::getEmail)
                .orElse("Unassigned")
                : "Unassigned";

        return taskMapper.mapToTaskDto(task, creatorEmail, assigneeEmail);
    }

    public void deleteTaskById(long taskId) {
        taskRepository.deleteById(taskId);
    }

    public TaskDto updateTask(long taskId, UpdateTaskDto updateTaskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        if (updateTaskDto.description() != null) {
            task.setDescription(updateTaskDto.description());
        }
        if (updateTaskDto.title() != null) {
            task.setTitle(updateTaskDto.title());
        }
        task = taskRepository.save(task);


        return getTaskDto(task);
    }

    public TaskDto assignTask(long taskId) {

        UUID uuid = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));
        task.setAssignedTo(uuid);

        task = taskRepository.save(task);
        return getTaskDto(task);
    }
}
