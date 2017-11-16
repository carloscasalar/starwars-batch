package com.starwars.batch.reader;

import com.starwars.batch.domain.Planet;
import org.springframework.batch.item.ItemReader;
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

        Planet nextPlanet = null;

        if (nextPlanetIndex < planetList.size()) {
            nextPlanet = planetList.get(nextPlanetIndex);
            nextPlanetIndex++;
        }

        return nextPlanet;
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
