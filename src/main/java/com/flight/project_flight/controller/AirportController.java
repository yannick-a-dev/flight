package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.mapper.AirportMapper;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.service.AirportService;
import com.flight.project_flight.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;
    private final FlightService flightService;

    public AirportController(AirportService airportService, FlightService flightService) {
        this.airportService = airportService;
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<AirportDTO>> getAllAirports() {
        List<AirportDTO> airportDTOs = airportService.getAllAirports();
        return ResponseEntity.ok(airportDTOs);
    }

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
        Airport airport = AirportMapper.toEntity(airportDTO, flightService);
        Airport savedAirport = airportService.addAirport(airport);
        AirportDTO createdAirportDTO = AirportMapper.toDTO(savedAirport);
        return ResponseEntity.status(201).body(createdAirportDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDTO> updateAirport(@PathVariable Long id, @RequestBody AirportDTO airportDTO) {
        Airport airportDetails = AirportMapper.toEntity(airportDTO, flightService);
        Airport updatedAirport = airportService.updateAirport(id, airportDetails);
        AirportDTO updatedAirportDTO = AirportMapper.toDTO(updatedAirport);
        return ResponseEntity.ok(updatedAirportDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAirport(@PathVariable Long id) {
        try {
            airportService.deleteAirport(id); // Suppression dans le service
            String message = "Airport with ID " + id + " has been successfully deleted.";
            return ResponseEntity.ok(message); // Réponse au format String
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Airport not found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting airport with ID " + id + ": " + e.getMessage());
        }
    }

}

