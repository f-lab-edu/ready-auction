package com.example.readyauction.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.readyauction.batch.job.RedisLikeReader;
import com.example.readyauction.batch.job.RedisLikeWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final ItemReader redisLikeReader;
	private final ItemWriter redisLikeWriter;

	public BatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
		RedisLikeReader redisLikeReader, RedisLikeWriter redisLikeWriter) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
		this.redisLikeReader = redisLikeReader;
		this.redisLikeWriter = redisLikeWriter;
	}

	@Bean
	public Job readLikeInRedis() {
		return new JobBuilder("readLikeInRedis", jobRepository)
			.start(updateStep())
			.build();
	}

	@Bean
	public Step updateStep() {
		return new StepBuilder("updateProductLikeDB", jobRepository)
			.chunk(500, platformTransactionManager)
			.reader(redisLikeReader)
			.writer(redisLikeWriter)
			.taskExecutor(new SimpleAsyncTaskExecutor())
			.transactionManager(platformTransactionManager)
			.build();
	}

}
