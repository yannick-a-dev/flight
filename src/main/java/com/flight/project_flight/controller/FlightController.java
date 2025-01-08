package com.flight.project_flight.controller;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.service.FlightService;
import jakarta.validation.Valid;
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

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();

        if (flights.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flights);
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<Flight> updateFlight(@PathVariable String flightNumber, @RequestBody FlightDto flightDto) {
        try {
            Flight updatedFlight = flightService.updateFlight(flightNumber, flightDto);
            return ResponseEntity.ok(updatedFlight);
        } catch (FlightNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidFlightDataException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        try {
            flightService.deleteFlight(flightNumber);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

