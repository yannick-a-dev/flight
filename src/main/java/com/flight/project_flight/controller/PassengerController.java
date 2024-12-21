package com.flight.project_flight.controller;

import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/passengers")
@RestController
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<Passenger> createPassenger(@RequestBody Passenger passenger) {
        if (passenger == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Passenger savedPassenger = passengerService.savePassenger(passenger);
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
    public ResponseEntity<Optional<Passenger>> getPassengerById(@PathVariable Long id) {
        Optional<Passenger> passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(@PathVariable Long id, @RequestBody Passenger passenger) {
        return passengerService.getPassengerById(id)
                .map(existingPassenger -> {
                    // Mettre à jour les propriétés du passager existant avec celles du corps de la requête
                    existingPassenger.setFirstName(passenger.getFirstName());
                    existingPassenger.setLastName(passenger.getLastName());
                    existingPassenger.setEmail(passenger.getEmail());
                    existingPassenger.setPhone(passenger.getPhone());
                    existingPassenger.setPassportNumber(passenger.getPassportNumber());
                    existingPassenger.setDob(passenger.getDob());
                    existingPassenger.setReservations(passenger.getReservations());
                    existingPassenger.setAlerts(passenger.getAlerts());

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

}
