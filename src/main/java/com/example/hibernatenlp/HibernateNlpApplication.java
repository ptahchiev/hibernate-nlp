package com.example.hibernatenlp;

import com.example.hibernatenlp.db.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@EnableBatchProcessing
public class HibernateNlpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HibernateNlpApplication.class, args);
    }

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /* Task executor */
    @Bean(name = { "defaultSearchExportTaskExecutor", "searchExportTaskExecutor" })
    public TaskExecutor defaultSearchExportTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.setThreadNamePrefix("search-export-");
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }


    /* Job */

    @Bean(name = "searchExportJob")
    @ConditionalOnMissingBean(name = "searchExportJob")
    public Job searchExportJob(JobBuilderFactory jobBuilders, @Qualifier("searchExportStep") final Step searchExportStep) {

        return jobBuilders.get("searchExportJob").start(searchExportStep).incrementer(new RunIdIncrementer()).build();
    }

    @JobScope
    @Bean(name = { "defaultSearchExportStep", "searchExportStep" })
    @ConditionalOnMissingBean(name = { "searchExportStep" })
    public Step defaultSearchExportStep(StepBuilderFactory stepBuilders, ItemStreamReader<ProductEntity> productReader,
                                        @Qualifier("searchExportProcessor") ItemProcessor<ProductEntity, Map<String, Object>> processor,
                                        @Qualifier("productWriter") ItemWriter<Map<String, Object>> writer,
                                        @Qualifier("searchExportTaskExecutor") TaskExecutor searchExportTaskExecutor) {

        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
        transactionAttribute.setIsolationLevel(TransactionAttribute.ISOLATION_READ_UNCOMMITTED);

        //@formatter:off
        return stepBuilders.get("search-export").<ProductEntity, Map<String, Object>>chunk(200)
                        .reader(productReader)
                        .processor(processor)
                        .writer(writer)
                        .transactionAttribute(transactionAttribute)
                        .taskExecutor(searchExportTaskExecutor).throttleLimit(4)
                        .build();
        //@formatter:on
    }

    @Transactional
    @StepScope
    @Bean(name = { "defaultSearchProductExportReader", "searchProductExportReader" })
    @ConditionalOnMissingBean(name = { "searchProductExportReader" })
    public ItemStreamReader<ProductEntity> defaultSearchProductExportReader(ProductRepository productRepository,
                                                                            @Qualifier("transactionManager") PlatformTransactionManager transactionManager,
                                                                            JobRepository jobRepository) {
        final RepositoryItemReader<ProductEntity> itemReader = new RepositoryItemReader<>();
        itemReader.setRepository(productRepository);

        itemReader.setMethodName("findAll");
        itemReader.setArguments(Collections.emptyList());
        itemReader.setSort(Collections.singletonMap("code", Sort.Direction.ASC));
        itemReader.setSaveState(false);

        SynchronizedItemStreamReader<ProductEntity> result = new SynchronizedItemStreamReader<>();
        result.setDelegate(itemReader);

        return result;
    }

    @StepScope
    @Bean(name = "searchExportProcessor")
    public ItemProcessor<ProductEntity, Map<String, Object>> defaultItemProcessor() {
        return productEntity -> {
            LOG.error("processing product: " + productEntity.getId() + " with name: " + productEntity.getName(new Locale("bg","BG")));
            return null;
        };
    }

    @StepScope
    @Bean(name = "productWriter")
    public ItemWriter<Map<String, Object>> defaultItemWriter() {
        return list -> {
            //
        };
    }

}
