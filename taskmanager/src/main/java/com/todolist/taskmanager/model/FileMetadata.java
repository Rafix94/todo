package com.todolist.taskmanager.model;

public record FileMetadata(
        Long fileId,
        String s3Key,
        ScanDetails scanDetails
) {
    public FileMetadata(Long fileId, String s3Key, ScanDetails scanDetails) {
        this.fileId = fileId;
        this.s3Key = s3Key;
        this.scanDetails = scanDetails != null ? scanDetails : new ScanDetails();
    }
}
