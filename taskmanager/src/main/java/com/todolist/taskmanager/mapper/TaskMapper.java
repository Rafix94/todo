package com.todolist.taskmanager.mapper;

import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.model.Task;
import com.todolist.taskmanager.dto.TaskDto;

public interface TaskMapper {
    static TaskDto mapToTaskDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedBy(),
                task.getAssignedTo(),
                task.getTeamId()
        );
    }

    static Task mapToTask(CreateTaskDto createTaskDto) {
        return Task.builder()
                .teamId(createTaskDto.teamId())
                .title(createTaskDto.title())
                .description(createTaskDto.description())
                .build();
    }

}
