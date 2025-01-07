package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.FlightAlertService;
import com.flight.project_flight.service.FlightService;
import com.flight.project_flight.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    private final FlightAlertService flightAlertService;
    private final PassengerService passengerService;
    private final FlightService flightService;

    public AlertController(FlightAlertService flightAlertService, PassengerService passengerService, FlightService flightService) {
        this.flightAlertService = flightAlertService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody AlertDto alertDto) {
        if (alertDto.getPassengerId() == null || alertDto.getFlightNumber() == null) {
            log.error("PassengerId or FlightId is missing in the request body.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        if (passenger == null) {
            log.error("Passenger not found with ID: " + alertDto.getPassengerId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Flight flight = flightService.findByFlightNumber(alertDto.getFlightNumber());
        if (flight == null) {
            log.error("Flight not found with ID: " + alertDto.getFlightNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Convertir severity en enum Severity
        Severity severity;
        try {
            severity = Severity.valueOf(alertDto.getSeverity().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid severity value: " + alertDto.getSeverity());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Comparable<? extends Comparable<?>> alertDate = alertDto.getAlertDate() != null ? alertDto.getAlertDate() : new Date();
        Alert alert = flightAlertService.createAlertForFlight(
                alertDto.getPassengerId(),
                alertDto.getFlightNumber(),
                alertDto.getMessage(),
                severity,
                (LocalDateTime) alertDate
        );

        if (alert == null) {
            log.error("Failed to create alert for Passenger ID: " + alertDto.getPassengerId() + " and Flight ID: " + alertDto.getFlightNumber());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    // Récupérer toutes les alertes
    @GetMapping("/all")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = flightAlertService.getAllAlerts();
        if (alerts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alerts);
    }

    // Récupérer une alerte par ID
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        Alert alert = flightAlertService.getAlertById(id);
        if (alert != null) {
            return ResponseEntity.ok(alert);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Récupérer les alertes pour un passager donné
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Alert>> getAlertsForPassenger(@PathVariable Long passengerId) {
        List<Alert> alerts = flightAlertService.getAlertsForPassenger(passengerId);
        if (alerts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(alerts);
    }
}
