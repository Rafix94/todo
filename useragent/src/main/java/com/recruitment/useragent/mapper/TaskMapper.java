package com.recruitment.useragent.mapper;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.entity.Task;

public interface TaskMapper {


    static TaskDto mapToTaskDto(Task task) {
        return TaskDto.builder()
                .title(task.getTitle())
                .dueDate(task.getDueDate())
                .category(task.getCategory())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();
    }
    static Task mapToTask(TaskDto taskDto) {
        return Task.builder()
                .title(taskDto.getTitle())
                .dueDate(taskDto.getDueDate())
                .category(taskDto.getCategory())
                .priority(taskDto.getPriority())
                .status(taskDto.getStatus())
                .build();
    }
}