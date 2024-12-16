package com.todolist.taskmanager.mapper;

import com.todolist.ScanningStatus;
import com.todolist.taskmanager.controller.FileDto;

import com.todolist.taskmanager.model.File;
import com.todolist.taskmanager.service.SpacesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileMapper {

    SpacesService spacesService;

    public FileDto mapToFileDto(File file) {
        if (file != null ){
            return new FileDto(
                    file.getId(),
                    file.getName(),
                    file.getSize(),
                    file.getScanningStatus() == ScanningStatus.CLEAN ?
                            spacesService.generatePresignedUrl(file.getS3Key()) : null,
                    file.getScanningStatus()
            );
        }
        return null;
    }
}
