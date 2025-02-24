package com.home.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TaskRunRequest {
    private Integer taskId;
    private UUID requestId;
    private Integer min;
    private Integer max;
    private Integer count;
}
