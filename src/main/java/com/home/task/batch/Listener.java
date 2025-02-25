package com.home.task.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Listener implements JobExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job is about to start for Job ID {} instance ID {}", jobExecution.getJobId(), jobExecution.getJobInstance().getInstanceId());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("Job ID {} instance ID {} has completed with status: {}", jobExecution.getJobId(),
                jobExecution.getJobInstance().getInstanceId(), jobExecution.getStatus());
    }
}
