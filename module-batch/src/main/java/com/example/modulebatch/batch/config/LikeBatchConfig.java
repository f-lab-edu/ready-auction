package com.example.modulebatch.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LikeBatchConfig {
    public static final int CHUNK_SIZE = 500;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final ItemReader redisLikeReader;
    private final ItemWriter redisLikeWriter;

    public LikeBatchConfig(JobRepository jobRepository,
                           PlatformTransactionManager platformTransactionManager,
                           @Qualifier("RedisLikeReader") ItemReader redisLikeReader,
                           @Qualifier("RedisLikeWriter") ItemWriter redisLikeWriter) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.redisLikeReader = redisLikeReader;
        this.redisLikeWriter = redisLikeWriter;
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
            .build();
    }
}
