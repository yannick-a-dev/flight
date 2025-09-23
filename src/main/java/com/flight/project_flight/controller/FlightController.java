package com.flight.project_flight.controller;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.dto.FlightResponseDto;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.FlightMapper;
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
    private final FlightMapper flightMapper;

    public FlightController(FlightService flightService, FlightMapper flightMapper) {
        this.flightService = flightService;
        this.flightMapper = flightMapper;
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
        FlightResponseDto responseDto = flightMapper.toResponseDto(createdFlight);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<FlightResponseDto>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();

        List<FlightResponseDto> dtos = flights.stream()
                .map(flightMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightResponseDto> getFlightByNumber(@PathVariable String flightNumber) {
        try {
            Flight flight = flightService.getFlightByNumber(flightNumber); // à implémenter dans service
            return ResponseEntity.ok(flightMapper.toResponseDto(flight));
        } catch (FlightNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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

