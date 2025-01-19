package com.example.modulebatch.batch.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.moduledomain.repository.product.ProductLikeRepository;

@SpringBootTest
@SpringBatchTest
public class LikeBatchConfigTest {

    @Autowired
    private Job readLikeInRedisJob;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Test
    public void 좋아요_Redis_Job_실행확인() throws Exception {
        // given
        String uniqueParameter = String.valueOf(System.currentTimeMillis());  // 현재 시간 기반
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
            .addString("chunkSize", "500")
            .addString("uniqueKey", uniqueParameter)  // 고유 키 추가
            .toJobParameters();

        //when: 배치 작업 실행
        jobLauncherTestUtils.setJob(readLikeInRedisJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then: 작업이 정상적으로 완료되었는지 확인
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
