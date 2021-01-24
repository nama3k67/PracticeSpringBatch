package fpt.project.chunkTasklet.config;

import fpt.project.chunkTasklet.chunks.LineProcessor;
import fpt.project.chunkTasklet.chunks.LineReader;
import fpt.project.chunkTasklet.chunks.LinesWriter;
import fpt.project.chunkTasklet.model.Line;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ChunksConfig {
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

    @Bean
    public ItemReader<Line> itemReader(){
        return new LineReader();
    }

    @Bean
    public ItemProcessor<Line, Line> itemProcessor(){
        return new LineProcessor();
    }

    @Bean
    public ItemWriter<Line> itemWriter(){
        return new LinesWriter();
    }

    @Bean
    protected Step processLines(ItemReader<Line> reader, ItemProcessor<Line, Line> processor, ItemWriter<Line> writer){
        return steps.get("processLines").<Line, Line> chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job() {
        return jobs
                .get("chunksJob")
                .start(processLines(itemReader(), itemProcessor(), itemWriter()))
                .build();
    }
}
