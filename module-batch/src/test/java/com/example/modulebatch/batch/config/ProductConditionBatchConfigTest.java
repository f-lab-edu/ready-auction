package com.example.modulebatch.batch.config;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBatchTest
@SpringBootTest
@Import(ProductConditionBatchConfig.class)
@EnableBatchProcessing
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
        String uniqueParameter = String.valueOf(System.currentTimeMillis());
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
                .addString("chunkSize", "500")
                .addString("uniqueKey", uniqueParameter)
                .toJobParameters();

        //when
        jobLauncherTestUtils.setJob(updateProductConditionJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

    @Test
    public void 경매상품_상태변경_job() throws Exception {
        // given
        List<Product> mockProducts = createMockProducts();
        productRepository.saveAll(mockProducts);

        String uniqueParameter = String.valueOf(System.currentTimeMillis());
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        JobParameters jobParameters = jobParametersBuilder
                .addString("chunkSize", "500")
                .addString("uniqueKey", uniqueParameter)
                .toJobParameters();

        //when
        jobLauncherTestUtils.setJob(updateProductConditionJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        List<Product> updatedProducts = productRepository.findAll();

        for (Product product : updatedProducts) {
            assertEquals(ProductCondition.DONE, product.getProductCondition());
        }
    }

    public static List<Product> createMockProducts() {
        Product product1 = Product.builder().userId("user1")
                                  .productName("Product 1")
                                  .category(Category.ELECTRONICS)
                                  .productCondition(ProductCondition.READY)
                                  .startDate(LocalDateTime.now())
                                  .closeDate(LocalDateTime.now().plusDays(7))
                                  .startPrice(1000L)
                                  .build();

        Product product2 = Product.builder()
                                  .userId("user2")
                                  .productName("Product 2")
                                  .category(Category.FASHION)
                                  .productCondition(ProductCondition.ACTIVE)
                                  .startDate(LocalDateTime.now().minusDays(1))
                                  .closeDate(LocalDateTime.now().plusDays(5))
                                  .startPrice(1000L)
                                  .build();

        return Arrays.asList(product1, product2);
    }

}
