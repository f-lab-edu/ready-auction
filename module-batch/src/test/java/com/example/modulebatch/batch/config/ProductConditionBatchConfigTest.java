package com.example.modulebatch.batch.config;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.context.annotation.Import;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.repository.product.ProductRepository;

@SpringBatchTest
@SpringBootTest
@Import(ProductConditionBatchConfig.class)
class ProductConditionBatchConfigTest {
    @Autowired
    private Job updateProductConditionJob;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void 경매상품_상태변경_job_실행확인() throws Exception {
        // given
        // when: 배치 작업 실행
        String uniqueParameter = String.valueOf(System.currentTimeMillis());  // 현재 시간 기반
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
            .addString("chunkSize", "500")
            .addString("uniqueKey", uniqueParameter)  // 고유 키 추가
            .toJobParameters();

        //when: 배치 작업 실행
        jobLauncherTestUtils.setJob(updateProductConditionJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then: 작업이 정상적으로 완료되었는지 확인
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        // 검증 로직 추가하기.
    }

    @Test
    public void updateProductConditionJobBatchTest() throws Exception {
        // given
        List<Product> mockProducts = createMockProducts();
        productRepository.saveAll(mockProducts);  // Mock 데이터를 DB에 저장

        String uniqueParameter = String.valueOf(System.currentTimeMillis());  // 현재 시간 기반
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
            .addString("chunkSize", "500")
            .addString("uniqueKey", uniqueParameter)  // 고유 키 추가
            .toJobParameters();

        //when: 배치 작업 실행
        jobLauncherTestUtils.setJob(updateProductConditionJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then: 작업이 정상적으로 완료되었는지 확인
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        List<Product> updatedProducts = productRepository.findAll();

        for (Product product : updatedProducts) {
            assertEquals(ProductCondition.DONE, product.getProductCondition());  // 상태가 DONE으로 변경되어야 함
        }
    }

    public static List<Product> createMockProducts() {
        Product product1 = Product.builder()
            .userId("user1")
            .productName("Product 1")
            .category(Category.ELECTRONICS)
            .productCondition(ProductCondition.READY)
            .startDate(LocalDateTime.now())
            .closeDate(LocalDateTime.now().plusDays(7))
            .startPrice(1000)
            .build();

        Product product2 = Product.builder()
            .userId("user2")
            .productName("Product 2")
            .category(Category.FASHION)
            .productCondition(ProductCondition.ACTIVE)
            .startDate(LocalDateTime.now().minusDays(1))  // 지난 날짜로 설정
            .closeDate(LocalDateTime.now().plusDays(5))
            .startPrice(1000)
            .build();

        return Arrays.asList(product1, product2);
    }

}
