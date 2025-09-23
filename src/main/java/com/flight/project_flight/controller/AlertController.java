package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.AlertService;
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
import java.util.Optional;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);
    private final FlightAlertService flightAlertService;
    private final PassengerService passengerService;
    private final FlightService flightService;
    private final AlertService alertService;
    private final AlertMapper alertMapper;

    public AlertController(FlightAlertService flightAlertService, PassengerService passengerService,
                           FlightService flightService, AlertService alertService, AlertMapper alertMapper) {
        this.flightAlertService = flightAlertService;
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.alertService = alertService;
        this.alertMapper = alertMapper;
    }

    @PostMapping
    public ResponseEntity<AlertDto> createAlert(@RequestBody AlertDto alertDto) {
        log.debug("Received request to create alert: {}", alertDto);
        if (alertDto.getPassengerId() == null || alertDto.getFlightNumber() == null) {
            log.error("PassengerId or FlightNumber is missing in the request: {}", alertDto);
            return ResponseEntity.badRequest().build();
        }
        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        if (passenger == null) {
            log.error("Passenger not found with ID: {}", alertDto.getPassengerId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Flight flight = flightService.findByFlightNumber(alertDto.getFlightNumber())
                .orElseThrow(() -> new FlightNotFoundException(alertDto.getFlightNumber()));

        Severity severity;
        try {
            severity = Severity.valueOf(alertDto.getSeverity().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid severity value: {}", alertDto.getSeverity(), e);
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime alertDate = alertDto.getAlertDate() != null ? alertDto.getAlertDate() : LocalDateTime.now();

        Alert alert = flightAlertService.createAlertForFlight(
                alertDto.getPassengerId(),
                alertDto.getFlightNumber(),
                alertDto.getMessage(),
                severity,
                alertDate
        );

        if (alert == null) {
            log.error("Failed to create alert");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(alertMapper.mapToDto(alert));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AlertDto>> getAllAlerts() {
        List<Alert> alerts = flightAlertService.getAllAlerts();
        List<AlertDto> dtos = alerts.stream()
                .filter(a -> a != null && a.getId() != null && a.getMessage() != null && a.getSeverity() != null)
                .map(alertMapper::mapToDto)
                .toList();
        if (dtos.isEmpty()) {
            log.warn("⚠️ Aucune alerte valide trouvée dans la base !");
            return ResponseEntity.noContent().build();
        }
        log.debug("✅ {} alertes valides envoyées au frontend", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDto> getAlertById(@PathVariable Long id) {
        Alert alert = flightAlertService.getAlertById(id);
        if (alert == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(alertMapper.mapToDto(alert));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<AlertDto>> getAlertsForPassenger(@PathVariable Long passengerId) {
        List<AlertDto> dtos = flightAlertService.getAlertsForPassenger(passengerId).stream()
                .map(alertMapper::mapToDto)
                .toList();

        if (dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{flightNumber}/alerts")
    public ResponseEntity<List<AlertDto>> getAlertsForFlight(@PathVariable String flightNumber) {
        List<AlertDto> dtos = alertService.getAlertsByFlightNumber(flightNumber).stream()
                .map(alertMapper::mapToDto)
                .toList();

        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }
}
