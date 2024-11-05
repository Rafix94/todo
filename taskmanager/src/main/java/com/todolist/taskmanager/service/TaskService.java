package com.todolist.taskmanager.service;

import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.dto.UpdateTaskDto;
import com.todolist.taskmanager.exception.NotFoundException;
import com.todolist.taskmanager.mapper.TaskMapper;
import com.todolist.taskmanager.model.Task;
import com.todolist.taskmanager.repository.TaskRepository;
import com.todolist.taskmanager.dto.TaskDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {
    TaskRepository taskRepository;

    public Page<TaskDto> getTasksByTeam(UUID teamId, Pageable pageable) {
        UUID uuid = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Page<Task> tasks = taskRepository.findByTeamId(teamId, pageable);

        return tasks.map(TaskMapper::mapToTaskDto);
    }

    public TaskDto createTask(CreateTaskDto createTaskDto) {
        Task task = taskRepository.save(TaskMapper.mapToTask(createTaskDto));
        return TaskMapper.mapToTaskDto(task);
    }

    public TaskDto getTaskById(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));
        return TaskMapper.mapToTaskDto(task);
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
        return TaskMapper.mapToTaskDto(task);
    }

    public TaskDto assignTaskToUser(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));
        UUID uuid = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        task.setAssignedTo(uuid);

        task = taskRepository.save(task);
        return TaskMapper.mapToTaskDto(task);
    }

}
