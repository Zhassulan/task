package com.home.task.service;

import com.home.task.dto.TaskRunRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("taskBatchService")
@RequiredArgsConstructor
@Slf4j
public class TaskServiceBatchImpl implements TaskService {

    private final JobLauncher jobLauncher;

    @Qualifier("processTaskJob")
    private final Job job;

    @Override
    public void run(Long entityId) {
        try {
             JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("entityId", entityId)
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(job, jobParameters);
            log.info("Job instance ID {} status is: {}", execution.getJobInstance().getInstanceId(), execution.getStatus());
        } catch (Exception e) {
            log.error("Job failed: ", e);
        }
    }
}
