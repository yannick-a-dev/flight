package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.AlertService;
import com.flight.project_flight.service.FlightService;
import com.flight.project_flight.service.PassengerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertService alertService;
    private final PassengerService passengerService;
    private final FlightService flightService;

    public AlertController(AlertService alertService, PassengerService passengerService, FlightService flightService) {
        this.alertService = alertService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody AlertDto alertDto) {
        if (alertDto.getPassengerId() == null || alertDto.getFlightId() == null) {
            log.error("PassengerId or FlightId is missing in the request body.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        if (passenger == null) {
            log.error("Passenger not found with ID: " + alertDto.getPassengerId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Flight flight = flightService.findById(alertDto.getFlightId());
        if (flight == null) {
            log.error("Flight not found with ID: " + alertDto.getFlightId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Date alertDate = alertDto.getAlertDate() != null ? alertDto.getAlertDate() : new Date();
        Alert alert = alertService.createAlertForPassenger(
                passenger,
                flight,
                alertDate,
                alertDto.getMessage(),
                alertDto.getSeverity()
        );

        if (alert == null) {
            log.error("Failed to create alert for Passenger ID: " + alertDto.getPassengerId() + " and Flight ID: " + alertDto.getFlightId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        if (alerts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si aucune alerte
        }
        return ResponseEntity.ok(alerts); // Retourne toutes les alertes
    }

    // Récupérer une alerte par son ID
    @GetMapping
    public ResponseEntity<?> getAlerts(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long passengerId) {
        if (id != null) {
            Alert alert = alertService.getAlertById(id);
            if (alert == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(alert);
        }
        if (passengerId != null) {
            return ResponseEntity.ok(alertService.getAlertsForPassenger(passengerId));
        }
        return ResponseEntity.badRequest().body("Please provide either 'id' or 'passengerId'");
    }

}
