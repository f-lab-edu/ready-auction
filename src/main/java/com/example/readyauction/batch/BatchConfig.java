package com.example.readyauction.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final int CHUNK_SIZE = 500;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ItemReader redisLikeReader;
    private final ItemWriter redisLikeWriter;

    private final ItemProcessor productConditionUpdate;
    private final ItemReader productReader;
    private final ItemWriter productConditionWriter;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
        ItemReader redisLikeReader, ItemWriter redisLikeWriter, ItemProcessor productConditionUpdate,
        ItemReader productReader, ItemWriter productConditionWriter) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.redisLikeReader = redisLikeReader;
        this.redisLikeWriter = redisLikeWriter;
        this.productConditionUpdate = productConditionUpdate;
        this.productReader = productReader;
        this.productConditionWriter = productConditionWriter;
    }

    @Bean
    public Job updateProductConditionJob() {
        return new JobBuilder("updateProductCondition", jobRepository)
            .start(updateProductConditionStep())
            .build();
    }

    @Bean
    public Step updateProductConditionStep() {
        return new StepBuilder("updateProductConditionStep", jobRepository)
            .chunk(CHUNK_SIZE, platformTransactionManager)
            .reader(productReader)
            .processor(productConditionUpdate)
            .writer(productConditionWriter)
            .build();
    }

    @Bean
    public Job readLikeInRedisJob() {
        return new JobBuilder("readLikeInRedisJob", jobRepository)
            .start(updateProductLikeStep())
            .build();
    }

    @Bean
    public Step updateProductLikeStep() {
        return new StepBuilder("updateProductLikeStep", jobRepository)
            .chunk(CHUNK_SIZE, platformTransactionManager)
            .reader(redisLikeReader)
            .writer(redisLikeWriter)
            .taskExecutor(new SimpleAsyncTaskExecutor())
            .transactionManager(platformTransactionManager)
            .build();
    }

}
