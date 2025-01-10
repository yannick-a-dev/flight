package com.flight.project_flight.service;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.repository.AirportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportService {

    @Autowired
    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    // Récupérer tous les aéroports
    public List<AirportDTO> getAllAirports() {
        // Récupération des aéroports et conversion en DTO
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(airport -> new AirportDTO(airport))
                .collect(Collectors.toList());
    }



    // Récupérer un aéroport par ID
    public Airport getAirportById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with id: " + id));
    }

    // Récupérer un aéroport par nom
    public Airport getAirportByName(String name) {
        return airportRepository.findByName(name);
    }

    // Ajouter un nouvel aéroport
    public Airport addAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    // Mettre à jour un aéroport existant
    public Airport updateAirport(Long id, Airport airportDetails) {
        Airport airport = getAirportById(id);
        airport.setName(airportDetails.getName());
        airport.setLocation(airportDetails.getLocation());
        airport.setCode(airportDetails.getCode());
        return airportRepository.save(airport);
    }

    // Supprimer un aéroport
    public void deleteAirport(Long id) {
        Airport airport = getAirportById(id);
        airportRepository.delete(airport);
    }
}

