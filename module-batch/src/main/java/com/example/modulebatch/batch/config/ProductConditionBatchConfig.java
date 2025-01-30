package com.example.modulebatch.batch.config;

import com.example.moduledomain.domain.product.Product;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductConditionBatchConfig {
    public static final int CHUNK_SIZE = 500;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final ItemProcessor productProcessor;
    private final ItemWriter productConditionWriter;

    public ProductConditionBatchConfig(JobRepository jobRepository,
                                       PlatformTransactionManager platformTransactionManager,
                                       EntityManagerFactory entityManagerFactory,
                                       @Qualifier("ProductProcessor") ItemProcessor productProcessor,
                                       @Qualifier("ProductConditionWriter") ItemWriter productConditionWriter) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.productProcessor = productProcessor;
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
                .reader(productReader())
                .processor(productProcessor)
                .writer(productConditionWriter)
                .build();
    }

    public JpaPagingItemReader<Product> productReader() {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productReader")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Product p")
                .build();
    }

}
