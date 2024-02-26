package com.recruitment.useragent.mapper;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {

    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    TaskDto taskToTaskDTO(Task task);

    Task taskDTOToTask(TaskDto taskDTO);
}