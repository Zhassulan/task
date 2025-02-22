package com.home.task.controller;

import com.home.task.dto.TaskRunRequest;
import com.home.task.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;

    @PostMapping("/task/async")
    public void runTaskAsync(@RequestParam(value = "min") @NotNull int min, @RequestParam(value = "max") @NotNull int max,
                             @RequestParam(value = "count") @NotNull int count) {
        TaskRunRequest req = TaskRunRequest.builder()
                .max(max)
                .min(min)
                .requestId(UUID.randomUUID())
                .build();
        taskService.runAsyncTask(req);
    }
}
