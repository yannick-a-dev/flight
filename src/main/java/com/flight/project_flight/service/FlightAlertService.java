package com.flight.project_flight.service;

import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
import com.flight.project_flight.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FlightAlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final PassengerService passengerService;
    private final AlertRepository alertRepository;
    @Lazy
    public FlightAlertService(FlightRepository flightRepository, FlightService flightService, PassengerService passengerService, AlertRepository alertRepository) {
        this.flightRepository = flightRepository;
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public Alert createAlertForFlight(Long passengerId, String flightNumber, String message, Severity severity, LocalDateTime alertDate) {
        Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            throw new EntityNotFoundException("Passenger not found with ID: " + passengerId);
        }

        Flight flight = flightService.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));
        Alert alert = new Alert();
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setAlertDate(alertDate != null ? alertDate : LocalDateTime.now());
        alert.setPassenger(passenger);
        flight.addAlert(alert);
        flightRepository.save(flight);
        return alert;
    }



    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id).orElse(null);
    }

    public List<Alert> getAlertsForPassenger(Long passengerId) {
        return alertRepository.findByPassengerId(passengerId);
    }
}

