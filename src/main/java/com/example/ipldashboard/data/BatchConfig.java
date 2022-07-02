package com.example.ipldashboard.data;

import javax.sql.DataSource;

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

import com.example.ipldashboard.model.Match;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final String[] FIELD_NAMES = new String[] {
            "id", "city", "date", "season", "matchNumber", "team1", "team2", "venue", "tossWinner", "tossDecision",
            "superOver", "winningTeam", "wonBy", "margin", "method", "playerOfMatch", "team1Players", "team2Players",
            "umpire1", "umpire2"
    };

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<MatchInput> reader() {
        return new FlatFileItemReaderBuilder<MatchInput>().name("MatchItemReader")
                .resource(new ClassPathResource("ipl-data.csv")).delimited()
                .names(FIELD_NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInput>() {
                    {
                        setTargetType(MatchInput.class);
                    }
                })
                .linesToSkip(1)
                .build();
    }

    @Bean
    public MatchDataProcessor matchDataProcessor() {
        return new MatchDataProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
        // return new JdbcBatchItemWriterBuilder<Match>()
        //         .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        //         .sql("INSERT INTO match (id , city , date , season , match_number , team1 , team2 , venue , toss_winner , toss_decision , super_over , winning_team , won_by , margin , method , player_of_match , team1_players , team2_players , umpire1 , umpire2)" 
        //         +" VALUES (:id , :city , :date , :season , :matchNumber , :team1 , :team2 , :venue , :tossWinner , :tossDecision , :superOver , :winningTeam , :wonBy , :margin , :method , :playerOfMatch , :team1Players , :team2Players , :umpire1 , :umpire2)")
        //         .dataSource(dataSource)
        //         .build();
        return new JdbcBatchItemWriterBuilder<Match>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO match (id , city , date , season , match_number , team1 , team2 , venue , toss_winner , toss_decision , super_over , winning_team , won_by , margin , method , player_of_match , team1Players , team2Players , umpire1 , umpire2)" 
                +" VALUES (:id , :city , :date , :season , :matchNumber , :team1 , :team2 , :venue , :tossWinner , :tossDecision , :superOver , :winningTeam , :wonBy , :margin , :method , :playerOfMatch , :team1Players , :team2Players , :umpire1 , :umpire2)")
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
    public Step step1(JdbcBatchItemWriter<Match> writer) {
        return stepBuilderFactory.get("step1")
                .<MatchInput, Match>chunk(10)
                .reader(reader())
                .processor(matchDataProcessor())
                .writer(writer)
                .build();
    }
}
