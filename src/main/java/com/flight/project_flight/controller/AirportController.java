package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AirlineDTO;
import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.mapper.AirportMapper;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.service.AirportService;
import com.flight.project_flight.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getAirports(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String code,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false) String action
    ) {

        // --- Stats ---
        if ("stats".equalsIgnoreCase(action)) {
            Map<String, Object> stats = airportService.getAirportStatistics();
            return ResponseEntity.ok(stats);
        }

        // --- Pagination ---
        if ("paginated".equalsIgnoreCase(action)) {
            List<String> allowedSortFields = List.of(
                    "id", "name", "location", "code", "capacity", "city", "country",
                    "international", "isActive", "terminalInfo", "timezone"
            );
            if (!allowedSortFields.contains(sortBy)) sortBy = "id";

            Page<AirportDTO> airportsPage = airportService.getPaginatedAirports(page, size, sortBy);
            return ResponseEntity.ok(airportsPage);
        }

        // --- Recherche ---
        if ("search".equalsIgnoreCase(action)) {
            List<AirportDTO> results = airportService.searchAirports(name, city, country, code, true);
            return ResponseEntity.ok(results);
        }

        // --- Par défaut, tout renvoyer ---
        List<AirportDTO> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncAirportsFromExternalAPI() {
        try {
            airportService.syncWithExternalAPI();
            return ResponseEntity.ok("Airports synchronized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la synchronisation : " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<AirportDTO> createAirport(@RequestBody AirportDTO airportDTO) {
        Airport airport = AirportMapper.toEntity(airportDTO);
        Airport savedAirport = airportService.addAirport(airport);
        AirportDTO createdAirportDTO = AirportMapper.toDTO(savedAirport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAirportDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportDTO> getAirportById(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().build();
        }
        Airport airport = airportService.getAirportById(Long.valueOf(id));
        if (airport != null) {
            return ResponseEntity.ok(AirportMapper.toDTO(airport));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/flights")
    public ResponseEntity<List<FlightDto>> getFlightsByAirport(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().build();
        }
        List<FlightDto> flights = flightService.getFlightsByAirport(Long.valueOf(id));
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}/airlines")
    public ResponseEntity<List<AirlineDTO>> getAirlinesByAirport(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().build();
        }
        List<AirlineDTO> airlines = airportService.getAirlinesByAirport(Long.valueOf(id));
        return ResponseEntity.ok(airlines);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDTO> updateAirport(@PathVariable Long id, @RequestBody AirportDTO airportDTO) {
        Airport airportDetails = AirportMapper.toEntity(airportDTO);
        Airport updatedAirport = airportService.updateAirport(id, airportDetails);
        AirportDTO updatedAirportDTO = AirportMapper.toDTO(updatedAirport);
        return ResponseEntity.ok(updatedAirportDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAirport(@PathVariable Long id) {
        try {
            airportService.deleteAirport(id);
            String message = "Airport with ID " + id + " has been successfully deleted.";
            return ResponseEntity.ok(message);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Airport not found with ID " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting airport with ID " + id + ": " + e.getMessage());
        }
    }

}
