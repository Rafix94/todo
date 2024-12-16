package com.todolist.taskmanager.mapper;

import com.todolist.taskmanager.dto.CommentDto;
import com.todolist.taskmanager.dto.CreateTaskDto;
import com.todolist.taskmanager.model.Comment;
import com.todolist.taskmanager.model.Task;
import com.todolist.taskmanager.dto.TaskDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TaskMapper {

    private final CommentMapper commentMapper;

    public TaskDto mapToTaskDto(Task task, String creatorEmail, String assigneeEmail) {

        if (task != null){
            List<Comment> comments = task.getComments();
            List<CommentDto> commentDtoStream = null;
            if (comments != null) {
                commentDtoStream = comments.stream()
                        .map(commentMapper::mapToCommentDto)
                        .toList();
            }
            return new TaskDto(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getCreatedBy(),
                    task.getAssignedTo(),
                    creatorEmail,
                    assigneeEmail,
                    task.getTeamId(),
                    commentDtoStream
                    );
        }
        return null;
    }

    public Task mapToTask(CreateTaskDto createTaskDto) {
        return Task.builder()
                .teamId(createTaskDto.teamId())
                .title(createTaskDto.title())
                .description(createTaskDto.description())
                .build();
    }

}
