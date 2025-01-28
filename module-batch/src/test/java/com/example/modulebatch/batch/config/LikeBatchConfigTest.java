package com.example.modulebatch.batch.config;

import com.example.moduledomain.repository.product.ProductLikeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
@EnableBatchProcessing
public class LikeBatchConfigTest {

    @Autowired
    private Job readLikeInRedisJob;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Test
    public void 좋아요_Redis_Job_실행확인() throws Exception {
        //given
        String uniqueParameter = String.valueOf(System.currentTimeMillis());
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
                .addString("chunkSize", "500")
                .addString("uniqueKey", uniqueParameter)
                .toJobParameters();

        //when
        jobLauncherTestUtils.setJob(readLikeInRedisJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
