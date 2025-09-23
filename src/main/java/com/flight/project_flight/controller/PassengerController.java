package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.mapper.PassengerMapper;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.service.AlertConverter;
import com.flight.project_flight.service.AlertService;
import com.flight.project_flight.service.FlightService;
import com.flight.project_flight.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/passengers")
@RestController
public class PassengerController {

    private final PassengerMapper passengerMapper;
    private final PassengerService passengerService;
    private final FlightService flightService;
    private final AlertService alertService;
    private final AlertConverter alertConverter;
    private final PasswordEncoder passwordEncoder;

    public PassengerController(PassengerMapper passengerMapper, PassengerService passengerService, FlightService flightService, AlertService alertService, AlertConverter alertConverter, PasswordEncoder passwordEncoder) {
        this.passengerMapper = passengerMapper;
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.alertService = alertService;
        this.alertConverter = alertConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Passenger> createPassenger(@RequestBody PassengerDTO passengerDTO) {
        if (passengerDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Passenger savedPassenger = passengerService.registerPassenger(passengerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPassenger);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PassengerDTO>> getAllPassengers() {
        List<Passenger> passengers = passengerService.getAllPassengers();
        List<PassengerDTO> passengerDTOs = passengers.stream()
                .map(passengerMapper::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(passengerDTOs);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getPassengerNames() {
        List<String> names = passengerService.getPassengerNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id)
                .map(passenger -> ResponseEntity.ok(passenger))
                .orElseGet(() -> {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null);
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable Long id, @RequestBody PassengerDTO passengerDTO) {
        return passengerService.getPassengerById(id)
                .map(existingPassenger -> {
                    passengerMapper.updatePassengerDetails(existingPassenger, passengerDTO);
                    if (passengerDTO.getPassword() != null && !passengerDTO.getPassword().isEmpty()) {
                        String encodedPassword = passwordEncoder.encode(passengerDTO.getPassword());
                        existingPassenger.setPassword(encodedPassword);
                        System.out.println("Mot de passe encodé : " + encodedPassword);  // Log du mot de passe encodé
                    }
                    if (existingPassenger.getPassword() == null || existingPassenger.getPassword().isEmpty()) {
                        existingPassenger.setPassword("default_password");
                        System.out.println("Mot de passe par défaut appliqué");
                    }
                    try {
                        List<Alert> updatedAlerts = updatePassengerAlerts(existingPassenger, passengerDTO);
                        existingPassenger.setAlerts(updatedAlerts);
                    } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().build();
                    }
                    Passenger updatedPassenger = passengerService.savePassenger(existingPassenger);
                    return ResponseEntity.ok(updatedPassenger);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private List<Alert> updatePassengerAlerts(Passenger passenger, PassengerDTO passengerDTO) {
        return Optional.ofNullable(passengerDTO.getAlerts())
                .orElse(Collections.emptyList())
                .stream()
                .map(alertDto -> {
                    Flight flight = passenger.getReservations().stream()
                            .map(Reservation::getFlight)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("No flight associated with passenger"));
                    Alert alert = alertConverter.convertToEntity(alertDto, passenger, flight);
                    return alertService.saveAlert(alert);
                })
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        Optional<Passenger> passengerOpt = passengerService.getPassengerById(id);
        if (passengerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Passenger passenger = passengerOpt.get();
        if (passenger.getAlerts() != null && !passenger.getAlerts().isEmpty()) {
            passenger.getAlerts().forEach(alert -> alertService.deleteAlertById(alert.getId()));
        }
        passengerService.deletePassengerById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{passengerId}/flights")
    public ResponseEntity<List<Flight>> getFlightsByPassenger(@PathVariable Long passengerId) {
        List<Flight> flights = flightService.getFlightsByPassenger(passengerId);
        return ResponseEntity.ok(flights);
    }

}
