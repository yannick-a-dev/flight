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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        if (passenger == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Flight flight = flightService.findById(alertDto.getFlightId());
        if (flight == null) {
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

        // Retourne l'alerte créée ou existante avec un statut 201
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }


    @GetMapping("/{passengerId}")
    public ResponseEntity<List<Alert>> getAlertsForPassenger(@PathVariable Long passengerId) {
        return ResponseEntity.ok(alertService.getAlertsForPassenger(passengerId));
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        if (alerts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si aucune alerte
        }
        return ResponseEntity.ok(alerts); // Retourne toutes les alertes
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        Alert alert = alertService.getAlertById(id);
        if (alert == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retourne 404 si l'alerte n'existe pas
        }
        return ResponseEntity.ok(alert); // Retourne l'alerte avec un statut 200
    }

}
