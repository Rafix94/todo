package com.todolist.taskmanager.service;

import com.todolist.taskmanager.model.Comment;
import com.todolist.taskmanager.model.FileMetadata;
import com.todolist.taskmanager.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.todolist.taskmanager.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class FileService {
    private final SpacesService spacesService;
    private final FileRepository fileRepository;
    private final KafkaTemplate<String, FileMetadata> kafkaTemplate;

    public File createFile(MultipartFile multipartFile, Comment comment) {

        java.io.File tempFile;
        try {
            tempFile = java.io.File.createTempFile("upload=", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String s3Key = "comments/" + comment.getId() + "/" + multipartFile.getOriginalFilename();
        spacesService.uploadFile(s3Key, tempFile);

        File file = File.builder()
                .size(multipartFile.getSize())
                .name(multipartFile.getOriginalFilename())
                .contentType(multipartFile.getContentType())
                .s3Key(s3Key)
                .comment(comment)
                .build();

        File savedFile = fileRepository.save(file);
        fileRepository.save(file);

        FileMetadata fileMetadata = new FileMetadata(savedFile.getId(), savedFile.getS3Key(), null);

        kafkaTemplate.send(String.valueOf(file.getId()), fileMetadata);

        return savedFile;
    }

}
