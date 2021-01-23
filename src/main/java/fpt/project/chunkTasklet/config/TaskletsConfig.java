package fpt.project.chunkTasklet.config;

import fpt.project.chunkTasklet.tasklets.LinesProcessor;
import fpt.project.chunkTasklet.tasklets.LinesReader;
import fpt.project.chunkTasklet.tasklets.LinesWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class TaskletsConfig {
    @Bean
    BatchConfigurer configurer(@Qualifier("dataSource") DataSource dataSource, PlatformTransactionManager transactionManager) {
        return new DefaultBatchConfigurer(dataSource) {
            @Override
            protected JobRepository createJobRepository() throws Exception {
                JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
                factory.setDataSource(dataSource);
                factory.setTransactionManager(transactionManager);
                factory.setSerializer(new Jackson2ExecutionContextStringSerializer());
                factory.afterPropertiesSet();
                return factory.getObject();
            }
        };
    }

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

//    @Bean
//    public JobLauncherTestUtils	jobLauncherTestUtils() {
//        return new JobLauncherTestUtils();
//    }

    @Bean
    public LinesReader linesReader(){
        return new LinesReader();
    }

    @Bean
    public LinesProcessor linesProcessor() {
        return new LinesProcessor();
    }

    @Bean
    public LinesWriter linesWriter() {
        return new LinesWriter();
    }

    @Bean
    protected Step readLines() {
        return steps
                .get("readLines")
                .tasklet(linesReader())
                .build();
    }

    @Bean
    protected Step processLines() {
        return steps
                .get("processLines")
                .tasklet(linesProcessor())
                .build();
    }

    @Bean
    protected Step writeLines() {
        return steps
                .get("writeLines")
                .tasklet(linesWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobs
                .get("taskletJob")
                .start(readLines())
                .next(processLines())
                .next(writeLines())
                .build();
    }
}
