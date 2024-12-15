package com.todolist.taskmanager.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScanDetails {
    private ScanningStatus scanningStatus;
    private ScanningResult scanningResult;
    private String virus;
}
