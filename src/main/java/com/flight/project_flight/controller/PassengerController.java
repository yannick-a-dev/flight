package com.flight.project_flight.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.mapper.PassengerMapper;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.service.*;
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
        try {
            return passengerService.getPassengerById(id)
                    .map(existingPassenger -> {

                        // Mise à jour des détails généraux
                        passengerMapper.updatePassengerDetails(existingPassenger, passengerDTO);

                        // Gestion du mot de passe
                        if (passengerDTO.getPassword() != null && !passengerDTO.getPassword().isEmpty()) {
                            String encodedPassword = passwordEncoder.encode(passengerDTO.getPassword());
                            existingPassenger.setPassword(encodedPassword);
                            System.out.println("Mot de passe encodé : " + encodedPassword);
                        } else if (existingPassenger.getPassword() == null || existingPassenger.getPassword().isEmpty()) {
                            String defaultPassword = passwordEncoder.encode("default_password");
                            existingPassenger.setPassword(defaultPassword);
                            System.out.println("Mot de passe par défaut encodé appliqué");
                        }

                        // Gestion des alertes
                        try {
                            List<Alert> updatedAlerts = updatePassengerAlerts(existingPassenger, passengerDTO);
                            existingPassenger.getAlerts().clear();
                            existingPassenger.getAlerts().addAll(updatedAlerts);
                        } catch (RuntimeException e) {
                            System.err.println("Erreur lors de la mise à jour des alertes : " + e.getMessage());
                            return ResponseEntity.badRequest().body("Impossible de mettre à jour les alertes : " + e.getMessage());
                        }

                        // Sauvegarde finale
                        Passenger updatedPassenger = passengerService.savePassenger(existingPassenger);
                        return ResponseEntity.ok(updatedPassenger);

                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne serveur : " + e.getMessage());
        }
    }

    private List<Alert> updatePassengerAlerts(Passenger passenger, PassengerDTO passengerDTO) {
        // Si pas d'alertes envoyées, retourne liste vide
        if (passengerDTO.getAlerts() == null || passengerDTO.getAlerts().isEmpty()) {
            return Collections.emptyList();
        }

        // Vérifie si le passager a au moins une réservation
        Optional<Flight> flightOpt = passenger.getReservations().stream()
                .map(Reservation::getFlight)
                .findFirst();
        if (flightOpt.isEmpty()) {
            throw new RuntimeException("Le passager n'a aucune réservation associée.");
        }
        Flight flight = flightOpt.get();

        // Conversion des alertes DTO en entité et sauvegarde
        return passengerDTO.getAlerts().stream()
                .map(alertDto -> {
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

        passengerService.deletePassenger(passengerOpt.get());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{passengerId}/flights")
    public ResponseEntity<List<Flight>> getFlightsByPassenger(@PathVariable Long passengerId) {
        List<Flight> flights = flightService.getFlightsByPassenger(passengerId);
        return ResponseEntity.ok(flights);
    }

}
