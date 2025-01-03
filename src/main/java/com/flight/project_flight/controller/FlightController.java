package com.flight.project_flight.controller;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }



    @GetMapping   @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody @Valid FlightDto flightDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Flight createdFlight = flightService.createFlight(flightDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
    }
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();

        if (flights.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flights);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        try {
            Flight updatedFlight = flightService.updateFlight(id, flight);
            return ResponseEntity.ok(updatedFlight);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
