package com.home.task.config;

import com.home.task.batch.TaskProcessor;
import com.home.task.batch.TaskWriter;
import com.home.task.entity.TaskEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TaskJobConfig {

    private final JobRepository jobRepository;
    private final TaskProcessor processor;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskWriter taskWriter;

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

        String name = "Process task step";
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
                .queryString("select r from TaskEntity r where r.successful = false")
                .pageSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<TaskEntity, TaskEntity> processor() {
        return processor;
    }

    @Bean
    public ItemWriter<TaskEntity> writer() throws Exception {
        return taskWriter;
    }
}
