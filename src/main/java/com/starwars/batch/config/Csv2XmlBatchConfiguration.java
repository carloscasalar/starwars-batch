package com.starwars.batch.config;

import com.starwars.batch.domain.People;
import com.starwars.batch.listener.JobListener;
import com.starwars.batch.listener.PeopleStepListener;
import com.starwars.batch.processor.PeopleProcessor;
import com.starwars.batch.repository.PeopleRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class Csv2XmlBatchConfiguration {

    @Bean
    public ItemReader<People> peopleReader() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"name", "birthYear", "gender", "height", "mass", "eyeColor", "hairColor", "skinColor"});

        BeanWrapperFieldSetMapper<People> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(People.class);

        DefaultLineMapper<People> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(lineTokenizer);

        FlatFileItemReader<People> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/people.csv"));
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);

        return itemReader;
    }

    @Bean
    public ItemWriter<People> peopleWriter() {
        StaxEventItemWriter<People> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/people.xml"));
        itemWriter.setRootTagName("peoples");
        itemWriter.setOverwriteOutput(true);

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(People.class);

        itemWriter.setMarshaller(marshaller);

        return itemWriter;
    }

    @Bean
    public ItemWriter<People> peopleDBWriter(PeopleRepository peopleRepository) {
        RepositoryItemWriter<People> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(peopleRepository);
        itemWriter.setMethodName("save");

        return itemWriter;
    }

    @Bean
    public ItemProcessor peopleItemProcessor() {
        return new PeopleProcessor();
    }

    @Bean
    public Step csvStep(StepBuilderFactory stepBuilderFactory,
                        ItemReader peopleReader,
                        ItemProcessor peopleItemProcessor,
                        ItemWriter peopleWriter) {

        return stepBuilderFactory
                .get("csvStep")
                .chunk(10)
                .reader(peopleReader)
                .processor(peopleItemProcessor)
                .writer(peopleWriter)
                .build();
    }

    @Bean
    public Step dbStep(StepBuilderFactory stepBuilderFactory,
                       ItemReader peopleReader,
                       ItemProcessor peopleItemProcessor,
                       ItemWriter peopleDBWriter,
                       PeopleStepListener peopleStepListener) {

        return stepBuilderFactory
                .get("dbStep")
                .chunk(10)
                .listener(peopleStepListener)
                .reader(peopleReader)
                .processor(peopleItemProcessor)
                .writer(peopleDBWriter)
                .build();
    }

    @Bean
    public Job csvJob(JobBuilderFactory jobBuilderFactory,
                      Step csvStep) {

        return jobBuilderFactory
                .get("csvJob")
                .incrementer(new RunIdIncrementer())
                .start(csvStep)
                .build();
    }

    @Bean
    public Job dbJob(JobBuilderFactory jobBuilderFactory,
                     Step dbStep,
                     JobListener jobListener) {

        return jobBuilderFactory
                .get("dbJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(dbStep)
                .build();
    }
}
















