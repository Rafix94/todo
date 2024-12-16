package com.todolist.taskmanager.mapper;

import com.todolist.taskmanager.dto.CommentDto;
import com.todolist.taskmanager.model.Comment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentMapper {

    FileMapper fileMapper;
    public CommentDto mapToCommentDto(Comment comment) {
        if (comment != null ){
            return new CommentDto(
                    comment.getId(),
                    comment.getComment(),
                    comment.getCreatedAt(),
                    fileMapper.mapToFileDto(comment.getFile())
            );
        }
        return null;
    }
}
