package com.warrencrasta.fantasy.yahoo.config.batch;


import com.warrencrasta.fantasy.yahoo.batchprocessing.StatsDataProcessor;
import com.warrencrasta.fantasy.yahoo.batchprocessing.StatsInput;
import com.warrencrasta.fantasy.yahoo.domain.stat.Stat;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  private final String[] FIELD_NAMES = new String[]{
          "Rk","Player","Pos","Age","Tm","G","GS","MP","FG","FGA","FGP","threeP","threePA","threePP","twoP","twoPA","twoPP","eFGP","FT","FTA","FTP","ORB","DRB","TRB","AST","STL","BLK","TOV","PF","PTS"
  };


  public final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  @Autowired
  public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public FlatFileItemReader<StatsInput> reader() {
    return new FlatFileItemReaderBuilder<StatsInput>()
            .name("personItemReader")
            .resource(new ClassPathResource("2023-stats-data.csv"))
            .delimited()
            .names(FIELD_NAMES)
            .fieldSetMapper(new BeanWrapperFieldSetMapper<StatsInput>() {{
              setTargetType(StatsInput.class);
            }})
            .build();
  }

  @Bean
  public StatsDataProcessor processor() {
    return new StatsDataProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Stat> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Stat>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO stat (Player, FGP, threePP, FTP, TRB, AST, STL, BLK, TOV, PTS) " +
                    " VALUES (:Player, :FGP, :threePP, :FTP, :TRB, :AST, :STL, :BLK, :TOV, :PTS)")
            .dataSource(dataSource)
            .build();
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("importUserJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<Stat> writer) {
    return stepBuilderFactory.get("step1")
            .<StatsInput, Stat> chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
  }



}
