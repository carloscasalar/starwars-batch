package com.starwars.batch.reader;

import com.starwars.batch.domain.Planet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlanetRestReader implements ItemReader<Planet> {
    private final String apiUrl = "http://swapi.co/api/planets/?format=json";
    private final RestTemplate restTemplate;

    private int nextPlanetIndex;
    private List<Planet> planetList;

    public PlanetRestReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        nextPlanetIndex = 0;
    }

    @Override
    public Planet read() throws Exception {
        if (planetListIsNotInitialized()) {
            planetList = fetchPlanetListFromAPI();
        }

        Planet nextStudent = null;

        if (nextPlanetIndex < planetList.size()) {
            nextStudent = planetList.get(nextPlanetIndex);
            nextPlanetIndex++;
        }

        return nextStudent;
    }

    private boolean planetListIsNotInitialized() {
        return this.planetList == null;
    }

    private List<Planet> fetchPlanetListFromAPI() {
        ResponseEntity<Planet[]> response = restTemplate.getForEntity(
                apiUrl,
                Planet[].class
        );
        Planet[] planets = response.getBody();
        return Arrays.asList(planets);
    }
}
