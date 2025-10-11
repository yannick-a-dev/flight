package com.flight.project_flight.external;

import com.flight.project_flight.dto.AirportDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ExternalAPIClient {

    private static final String API_URL = "https://api.example.com/airports";

    private final RestTemplate restTemplate;

    public ExternalAPIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AirportDTO> fetchAirports() {
        AirportDTO[] response = restTemplate.getForObject(API_URL, AirportDTO[].class);
        return List.of(response);
    }
}
