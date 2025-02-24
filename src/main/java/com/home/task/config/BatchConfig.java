package com.home.task.config;

import com.home.task.batch.TaskProcessor;
import com.home.task.batch.TaskWriter;
import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final TasksJpaRepository tasksJpaRepository;
    private final TaskProcessor taskProcessor;
    private final TaskWriter taskWriter;

    @Bean
    public Job job(JobRepository jobRepository, @Qualifier("step1") Step step1) {
        return new JobBuilder("firstBatchJob", jobRepository).preventRestart().start(step1).build();
    }

    public ItemReader<TaskEntity> reader(UUID id) {
        return new RepositoryItemReaderBuilder<TaskEntity>().repository(tasksJpaRepository)
                .methodName("findByRequestId")
                .arguments(id)
                .build();
    }

    @Bean
    public org.springframework.batch.item.ItemProcessor<TaskRunRequest, TaskEntity> processor() {
        return taskProcessor;
    }

    @Bean
    public ItemWriter<TaskEntity> writer() {
        return taskWriter;
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ItemReader<TaskEntity> reader,
                      @Qualifier("taskProcessor") org.springframework.batch.item.ItemProcessor<TaskEntity, TaskEntity> processor,
                      ItemWriter<TaskEntity> writer) {

        return new StepBuilder("step1", jobRepository).<TaskEntity, TaskEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
