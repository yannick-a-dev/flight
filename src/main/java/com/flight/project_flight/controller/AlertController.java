package com.flight.project_flight.controller;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.AlertResponseDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    public AlertController(FlightAlertService flightAlertService,
                           PassengerService passengerService,
                           FlightService flightService,
                           AlertService alertService,
                           AlertMapper alertMapper) {
        this.flightAlertService = flightAlertService;
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.alertService = alertService;
        this.alertMapper = alertMapper;
    }

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<AlertResponseDto> createAlert(@RequestBody AlertDto alertDto) {

        if (alertDto.getPassengerId() == null || alertDto.getFlightNumber() == null) {
            return ResponseEntity.badRequest().build();
        }

        Passenger passenger = passengerService.findById(alertDto.getPassengerId());

        Flight flight = flightService.findByFlightNumber(alertDto.getFlightNumber())
                .orElseThrow(() -> new FlightNotFoundException(alertDto.getFlightNumber()));

        LocalDateTime alertDate = alertDto.getAlertDate() != null
                ? alertDto.getAlertDate()
                : LocalDateTime.now();

        Alert alert = alertService.createAlertForPassenger(
                passenger,
                flight,
                alertDate,
                alertDto.getMessage(),
                alertDto.getSeverity()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertMapper.toResponseDto(alert));
    }

    // ✅ GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<AlertResponseDto>> getAllAlerts() {

        List<Alert> alerts = flightAlertService.getAllAlerts();

        List<AlertResponseDto> dtos = alerts.stream()
                .filter(a -> a != null && a.getId() != null)
                .map(alertMapper::toResponseDto)
                .toList();

        if (dtos.isEmpty()) {
            log.warn("⚠️ Aucune alerte valide trouvée !");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dtos);
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDto> getAlertById(@PathVariable Long id) {

        Alert alert = flightAlertService.getAlertById(id);

        if (alert == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(alertMapper.toResponseDto(alert));
    }

    // ✅ GET BY PASSENGER
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<AlertResponseDto>> getAlertsForPassenger(@PathVariable Long passengerId) {

        List<AlertResponseDto> dtos = flightAlertService.getAlertsForPassenger(passengerId).stream()
                .map(alertMapper::toResponseDto)
                .toList();

        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }

    // ✅ GET BY FLIGHT
    @GetMapping("/{flightNumber}/alerts")
    public ResponseEntity<List<AlertResponseDto>> getAlertsForFlight(@PathVariable String flightNumber) {

        List<AlertResponseDto> dtos = alertService.getAlertsByFlightNumber(flightNumber).stream()
                .map(alertMapper::toResponseDto)
                .toList();

        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dtos);
    }
}
