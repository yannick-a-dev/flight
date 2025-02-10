package com.flight.project_flight.controller;

import com.flight.project_flight.dto.PassengerDTO;
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

    private final PassengerService passengerService;
    private final FlightService flightService;
    private final AlertService alertService;
    private final AlertConverter alertConverter;
    private final PasswordEncoder passwordEncoder;

    public PassengerController(PassengerService passengerService, FlightService flightService, AlertService alertService, AlertConverter alertConverter, PasswordEncoder passwordEncoder) {
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
    public ResponseEntity<?> updatePassenger(@PathVariable Long id, @RequestBody PassengerDTO passengerDTO) {
        return passengerService.getPassengerById(id)
                .map(existingPassenger -> {
                    // Mettre à jour les propriétés du passager
                    updatePassengerDetails(existingPassenger, passengerDTO);

                    // Vérifier si le mot de passe est fourni
                    if (passengerDTO.getPassword() != null && !passengerDTO.getPassword().isEmpty()) {
                        // Encoder le mot de passe avant de le stocker
                        String encodedPassword = passwordEncoder.encode(passengerDTO.getPassword());
                        existingPassenger.setPassword(encodedPassword);
                        System.out.println("Mot de passe encodé : " + encodedPassword);  // Log du mot de passe encodé
                    }

                    // Appliquer l'encodage si nécessaire
                    if (existingPassenger.getPassword() == null || existingPassenger.getPassword().isEmpty()) {
                        existingPassenger.setPassword("default_password");  // Appliquer un mot de passe par défaut si nécessaire
                        System.out.println("Mot de passe par défaut appliqué");  // Log pour le mot de passe par défaut
                    }

                    // Mettre à jour les alertes, tout en gérant les potentiels cas d'absence de vol ou d'alertes nulles
                    try {
                        List<Alert> updatedAlerts = updatePassengerAlerts(existingPassenger, passengerDTO);
                        existingPassenger.setAlerts(updatedAlerts);
                    } catch (RuntimeException e) {
                        // Renvoie une réponse avec une erreur sans corps
                        return ResponseEntity.badRequest().build(); // Gérer l'absence de vol lié au passager
                    }

                    // Sauvegarder le passager mis à jour
                    Passenger updatedPassenger = passengerService.savePassenger(existingPassenger);
                    return ResponseEntity.ok(updatedPassenger);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Méthode pour mettre à jour les détails du passager
    private void updatePassengerDetails(Passenger passenger, PassengerDTO passengerDTO) {
        passenger.setFirstName(passengerDTO.getFirstName());
        passenger.setLastName(passengerDTO.getLastName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPhone(passengerDTO.getPhone());
        passenger.setPassportNumber(passengerDTO.getPassportNumber());
        passenger.setDob(passengerDTO.getDob());
    }

    // Méthode pour mettre à jour les alertes du passager
    private List<Alert> updatePassengerAlerts(Passenger passenger, PassengerDTO passengerDTO) {
        return Optional.ofNullable(passengerDTO.getAlerts())
                .orElse(Collections.emptyList()) // Initialiser une liste vide si aucune alerte n'est fournie
                .stream()
                .map(alertDto -> {
                    // Trouver le premier vol lié au passager via ses réservations
                    Flight flight = passenger.getReservations().stream()
                            .map(Reservation::getFlight)
                            .findFirst() // Supposant qu'un passager est lié à au moins un vol
                            .orElseThrow(() -> new RuntimeException("No flight associated with passenger"));

                    // Convertir le DTO en entité et sauvegarder l'alerte
                    Alert alert = alertConverter.convertToEntity(alertDto, passenger, flight);
                    return alertService.saveAlert(alert);
                })
                .collect(Collectors.toList());
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
