package com.home.task.controller;

import com.home.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;

}
