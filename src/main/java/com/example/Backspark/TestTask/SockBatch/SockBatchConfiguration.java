package com.example.Backspark.TestTask.SockBatch;

import com.example.Backspark.TestTask.exceptionHandling.IncorrectValueException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
public class SockBatchConfiguration {

    @StepScope
    @Bean
    public FlatFileItemReader<SockBatchPayload> csvReader(@Value("#{jobParameters['file.csv']}") String file) {
        return new FlatFileItemReaderBuilder<SockBatchPayload>()
                .name("csv-reader")
                .resource(new FileSystemResource("src/test/resources/" + file))
                .targetType(SockBatchPayload.class)
                .delimited()
                .delimiter(";")
                .names("sockColor", "cotton", "quantity")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<SockBatchPayload> dbWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<SockBatchPayload>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO sock (sock_color, cotton, quantity)
                        VALUES (:sockColor, :cotton, :quantity)
                        ON CONFLICT (sock_color, cotton)
                        DO UPDATE SET
                        quantity = sock.quantity+:quantity;""")
                .beanMapped()
                .build();
    }

    @Bean
    public ItemProcessor<SockBatchPayload, SockBatchPayload> sockItemProcessor() {
        return item -> {
            String errorMessage = "";
            String itemAsString = item.toString();
            if (item.getSockColor().length() < 3 || item.getSockColor().length() > 200) {
                errorMessage += "В строке " + itemAsString +
                        " количество символов в названии цвета должно быть в диапазоне от 3 до 200";
            }
            if (item.getCotton() == null) {
                errorMessage += "В строке " + itemAsString + " процент содержания хлопка должен быть указан";
            } else if (item.getCotton() > 100 || item.getCotton() < 0) {
                errorMessage += "В строке " + itemAsString +
                        " процент содержания хлопка должен быть в диапазоне от 0 до 100";
            }
            if (item.getQuantity() == null) {
                errorMessage += "В строке " + itemAsString + " количество должено быть указано";
            } else if (item.getQuantity() < 0) {
                errorMessage += "В строке " + itemAsString + " количество не может быть меньше 0";
            }
            if (!errorMessage.isEmpty()) {
                throw new IncorrectValueException(errorMessage);
            }
            return item;
        };
    }

    @Bean
    public Step loadCsvStep(FlatFileItemReader<SockBatchPayload> reader, JdbcBatchItemWriter<SockBatchPayload> dbWriter,
                            ItemProcessor<SockBatchPayload, SockBatchPayload> sockItemProcessor,
                            StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("load-csv-file")
                .<SockBatchPayload, SockBatchPayload>chunk(10)
                .reader(reader)
                .processor(sockItemProcessor)

                .writer(dbWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job importSockFromCSVFileJob(JobBuilderFactory jobBuilderFactory, Step loadCsvStep) {
        return jobBuilderFactory.get("import-sockFromCSVFile-job")
                .incrementer(new RunIdIncrementer())
                .start(loadCsvStep)
                .build();
    }
}
