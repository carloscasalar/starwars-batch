package com.starwars.batch.config;

import com.starwars.batch.domain.Planet;
import com.starwars.batch.reader.PlanetRestReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class PlanetsBatchConfig {


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                //.rootUri("http://swapi.co/api/planets/?format=json")
                .build();
    }

    @Bean
    public ItemReader<Planet> restPlanetReader(RestTemplate restTemplate) {
        return new PlanetRestReader(restTemplate);
    }


    @Bean
    public ItemWriter<Planet> planetWriter() {
        StaxEventItemWriter<Planet> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/planet.xml"));
        itemWriter.setRootTagName("planets");
        itemWriter.setOverwriteOutput(true);

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Planet.class);

        itemWriter.setMarshaller(marshaller);

        return itemWriter;
    }

    @Bean
    public Step planetCsvStep(StepBuilderFactory stepBuilderFactory,
                              ItemReader restPlanetReader,
                              ItemWriter planetWriter) {
        return stepBuilderFactory
                .get("planetCsvStep")
                .chunk(3)
                .reader(restPlanetReader)
                .writer(planetWriter)
                .build();
    }

    @Bean
    public Job planetJob(JobBuilderFactory jobBuilderFactory,
                         Step planetCsvStep) {
        return jobBuilderFactory
                .get("planetJob")
                .incrementer(new RunIdIncrementer())
                .start(planetCsvStep)
                .build();
    }
}
