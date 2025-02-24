package com.home.task.config;

import com.home.task.batch.TaskEntityItemProcessor;
import com.home.task.batch.TaskEntityWriter;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final TasksJpaRepository tasksJpaRepository;
    private final TaskEntityItemProcessor taskEntityItemProcessor;
    private final TaskEntityWriter taskEntityWriter;

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
    public ItemProcessor<TaskEntity, TaskEntity> processor() {
        return taskEntityItemProcessor;
    }

    @Bean
    public ItemWriter<TaskEntity> writer() {
        return taskEntityWriter;
    }
}
