package com.example.readyauction.batch;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.readyauction.config.BatchConfig;

@Component
public class BatchScheduler {
	private final JobLauncher jobLauncher;
	private final BatchConfig batchConfig;

	public BatchScheduler(JobLauncher jobLauncher, BatchConfig batchConfig) {
		this.jobLauncher = jobLauncher;
		this.batchConfig = batchConfig;
	}

	@Scheduled(cron = "0 0 6 * * *") // 매일 오전 6시
	public void runJob() {
		JobParameters params = new JobParametersBuilder()
			.addDate("jobDate", new Date())
			.toJobParameters();
		try {
			jobLauncher.run(batchConfig.readLikeInRedis(), params);
		} catch (Exception e) {
			// 예외 처리
		}
	}
}
