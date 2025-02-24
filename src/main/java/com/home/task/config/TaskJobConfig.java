package com.home.task.config;

import com.home.task.batch.TaskProcessor;
import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class TaskJobConfig {

    private final JobRepository jobRepository;
    private final TasksJpaRepository repository;
    private final TaskProcessor processor;
    private final EntityManagerFactory entityManagerFactory;
    private final LockRegistry lockRegistry;
    private final TasksJpaRepository tasksJpaRepository;

    @Bean
    public Job processTaskJob(Step step1) {
        String name = "Task Job";
        JobBuilder builder = new JobBuilder(name, jobRepository);
        return builder.start(step1).build();
    }

    @Bean
    public Step step1(ItemReader<TaskEntity> reader,
                      ItemWriter<TaskEntity> writer,
                      ItemProcessor<TaskEntity, TaskEntity> processor,
                      PlatformTransactionManager txManager) {

        var name = "Process task step";
        return new StepBuilder(name, jobRepository)
                .<TaskEntity, TaskEntity>chunk(5, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public JpaPagingItemReader itemReader() {
        return new JpaPagingItemReaderBuilder<TaskEntity>()
                .name("taskReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from TaskResult c")
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TaskEntity, TaskEntity> processor(@Value("#{jobParameters}") Map<String, Object> params) {
        processor.setJobParameters(params);
        return processor;
    }
}
