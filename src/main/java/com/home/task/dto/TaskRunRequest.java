package com.home.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TaskRunRequest {
    private Long taskId;
    private UUID requestId;
    private int min;
    private int max;
    private int count;
}
