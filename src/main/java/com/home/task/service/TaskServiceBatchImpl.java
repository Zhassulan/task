package com.home.task.service;

import com.home.task.dto.TaskRunRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service("taskBatchService")
@RequiredArgsConstructor
@Slf4j
public class TaskServiceBatchImpl implements TaskService {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Override
    public void run(TaskRunRequest request) {
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            log.info("Job Status : {}", execution.getStatus());
            log.info("Job completed");
        } catch (Exception e) {
            log.error("Job failed: ", e);
        }
    }
}
