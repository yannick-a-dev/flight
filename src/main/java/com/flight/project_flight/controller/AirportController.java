package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.mapper.AirportMapper;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.service.AirportService;
import com.flight.project_flight.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;
    private final FlightService flightService;

    public AirportController(AirportService airportService, FlightService flightService) {
        this.airportService = airportService;
        this.flightService = flightService;
    }

    // Récupérer tous les aéroports
    @GetMapping
    public ResponseEntity<List<AirportDTO>> getAllAirports() {
        List<AirportDTO> airportDTOs = airportService.getAllAirports();
        return ResponseEntity.ok(airportDTOs);
    }

    // Récupérer un aéroport par nom
    @GetMapping("/{id}")
    public ResponseEntity<AirportDTO> getAirportById(@PathVariable Long id) {
        Airport airport = airportService.getAirportById(id);

        if (airport != null) {
            AirportDTO airportDTO = AirportMapper.toDTO(airport);
            return ResponseEntity.ok(airportDTO);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<AirportDTO> createAirport(@RequestBody AirportDTO airportDTO) {
        // Si departureFlightIds est null, initialisez-le en tant que Set<String>
        if (airportDTO.getDepartureFlightIds() == null) {
            airportDTO.setDepartureFlightIds(new HashSet<>());
        }

        // Si arrivalFlightIds est null, initialisez-le en tant que Set<String>
        if (airportDTO.getArrivalFlightIds() == null) {
            airportDTO.setArrivalFlightIds(new HashSet<>());
        }

        // Mapper l'AirportDTO à l'entité Airport
        Airport airport = AirportMapper.toEntity(airportDTO, flightService);

        // Sauvegarder l'aéroport dans la base de données
        Airport savedAirport = airportService.addAirport(airport);

        // Mapper l'aéroport sauvegardé en AirportDTO
        AirportDTO createdAirportDTO = AirportMapper.toDTO(savedAirport);

        // Retourner la réponse avec le statut 201
        return ResponseEntity.status(201).body(createdAirportDTO);
    }



    // Mettre à jour un aéroport
    @PutMapping("/{id}")
    public ResponseEntity<AirportDTO> updateAirport(@PathVariable Long id, @RequestBody AirportDTO airportDTO) {
        Airport airportDetails = AirportMapper.toEntity(airportDTO, flightService);
        Airport updatedAirport = airportService.updateAirport(id, airportDetails);
        AirportDTO updatedAirportDTO = AirportMapper.toDTO(updatedAirport);
        return ResponseEntity.ok(updatedAirportDTO);
    }

    // Supprimer un aéroport
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }
}

