package com.flight.project_flight.service;

import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FlightAlertService {

    private final FlightService flightService;
    private final PassengerService passengerService;
    private final AlertService alertService;
    private final AlertRepository alertRepository;

    public FlightAlertService(FlightService flightService, PassengerService passengerService, AlertService alertService, AlertRepository alertRepository) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.alertService = alertService;
        this.alertRepository = alertRepository;
    }

    public Alert createAlertForFlight(Long passengerId, String flightNumber, String message, Severity severity, LocalDateTime alertDate) {
        // Récupérer les informations sur le passager
        Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            return null;
        }

        // Récupérer les informations sur le vol
        Flight flight = flightService.findByFlightNumber(flightNumber);
        if (flight == null) {
            return null;
        }

        // Créer l'alerte
        return alertService.createAlertForPassenger(passenger, flight, alertDate, message, String.valueOf(severity));
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

