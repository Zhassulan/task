package com.home.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskRunRequest {
    private Long taskId;
    private int min;
    private int max;
    private int count;
}
