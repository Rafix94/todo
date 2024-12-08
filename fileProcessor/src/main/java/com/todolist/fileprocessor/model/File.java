package com.todolist.fileprocessor.model;

public record File (
        Long fileId,
        String bucketName,
        String fileKey
)
{}
