package com.recruitment.useragent.mapper;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.entity.Task;

public interface TaskMapper {


    public static TaskDto mapToTaskDto(Task task) {
        return TaskDto.builder()
                .title(task.getTitle())
                .dueDate(task.getDueDate())
                .category(task.getCategory())
                .priority(task.getPriority())
                .status(task.getStatus())
                .build();
    }
}