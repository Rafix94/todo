package com.todolist;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScanDetails {
    private ScanningStatus scanningStatus;
    private String virus;
}
