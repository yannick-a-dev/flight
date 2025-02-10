package com.flight.project_flight.service;

import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
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

    private final FlightService flightService;
    private final PassengerService passengerService;
    private final AlertService alertService;
    private final AlertRepository alertRepository;
    @Lazy
    public FlightAlertService(FlightService flightService, PassengerService passengerService, AlertService alertService, AlertRepository alertRepository) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.alertService = alertService;
        this.alertRepository = alertRepository;
    }

    public Alert createAlertForFlight(Long passengerId, String flightNumber, String message, Severity severity, LocalDateTime alertDate) {
        log.debug("Starting to create alert for Passenger ID: {}, Flight Number: {}, Message: {}, Severity: {}, Alert Date: {}",
                passengerId, flightNumber, message, severity, alertDate);

        log.debug("Retrieving passenger with ID: {}", passengerId);
        Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            log.warn("No passenger found with ID: {}", passengerId);
            return null;
        }
        log.info("Passenger retrieved successfully: {}", passenger);

        log.debug("Retrieving flight with Flight Number: {}", flightNumber);
        Flight flight = flightService.findByFlightNumber(flightNumber).orElse(null);
        if (flight == null) {
            log.warn("No flight found with Flight Number: {}", flightNumber);
            return null;
        }
        log.info("Flight retrieved successfully: {}", flight);

        log.debug("Creating alert for Passenger: {} and Flight: {}", passenger, flight);
        Alert alert = alertService.createAlertForPassenger(passenger, flight, alertDate, message, String.valueOf(severity));
        if (alert != null) {
            log.info("Alert created successfully: {}", alert);
        } else {
            log.warn("Failed to create alert for Passenger: {} and Flight: {}", passenger, flight);
        }

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

