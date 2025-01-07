package com.flight.project_flight.controller;

import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.models.*;
import com.flight.project_flight.service.AlertConverter;
import com.flight.project_flight.service.AlertService;
import com.flight.project_flight.service.FlightService;
import com.flight.project_flight.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/passengers")
@RestController
public class PassengerController {

    private final PassengerService passengerService;
    private final FlightService flightService;
    private final AlertService alertService;
    private final AlertConverter alertConverter;

    public PassengerController(PassengerService passengerService, FlightService flightService, AlertService alertService, AlertConverter alertConverter) {
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.alertService = alertService;
        this.alertConverter = alertConverter;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Passenger> createPassenger(@RequestBody PassengerRequest passenger) {
        if (passenger == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Passenger savedPassenger = passengerService.registerPassenger(passenger);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPassenger);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
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
    public ResponseEntity<Passenger> updatePassenger(@PathVariable Long id, @RequestBody PassengerDTO passengerDTO) {
        return passengerService.getPassengerById(id)
                .map(existingPassenger -> {
                    // Mettre à jour les propriétés du passager
                    existingPassenger.setFirstName(passengerDTO.getFirstName());
                    existingPassenger.setLastName(passengerDTO.getLastName());
                    existingPassenger.setEmail(passengerDTO.getEmail());
                    existingPassenger.setPhone(passengerDTO.getPhone());
                    existingPassenger.setPassportNumber(passengerDTO.getPassportNumber());
                    existingPassenger.setDob(passengerDTO.getDob());

                    // Mettre à jour les alertes - Avoid NullPointerException by checking null
                    List<Alert> updatedAlerts = Optional.ofNullable(passengerDTO.getAlerts())
                            .orElse(Collections.emptyList()) // If null, initialize with empty list
                            .stream()
                            .map(alertDto -> {
                                // Get the first Flight associated with the Passenger via Reservations
                                Flight flight = existingPassenger.getReservations().stream()
                                        .map(Reservation::getFlight)
                                        .findFirst()  // Assuming one flight per passenger
                                        .orElseThrow(() -> new RuntimeException("No flight associated with passenger"));

                                // Convert DTO to entity and save the alert
                                Alert alert = alertConverter.convertToEntity(alertDto, existingPassenger, flight);
                                return alertService.saveAlert(alert);
                            })
                            .collect(Collectors.toList());

                    existingPassenger.setAlerts(updatedAlerts);

                    // Sauvegarder le passager mis à jour
                    Passenger updatedPassenger = passengerService.savePassenger(existingPassenger);
                    return ResponseEntity.ok(updatedPassenger);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        boolean isDeleted = passengerService.deletePassengerById(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Code 204 : La ressource a été supprimée avec succès
        } else {
            return ResponseEntity.notFound().build(); // Code 404 : Le passager n'a pas été trouvé
        }
    }

    @GetMapping("/{passengerId}/flights")
    public ResponseEntity<List<Flight>> getFlightsByPassenger(@PathVariable Long passengerId) {
        List<Flight> flights = flightService.getFlightsByPassenger(passengerId);
        return ResponseEntity.ok(flights);
    }

}
