package com.warrencrasta.fantasy.yahoo.config.batch;


import com.warrencrasta.fantasy.yahoo.batchprocessing.StatsDataProcessor;
import com.warrencrasta.fantasy.yahoo.batchprocessing.StatsInput;
import com.warrencrasta.fantasy.yahoo.domain.stat.Stat;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

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
            .sql("INSERT INTO stat (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .build();
  }



}
