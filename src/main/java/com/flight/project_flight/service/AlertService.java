package com.flight.project_flight.service;

import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
import com.flight.project_flight.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    private final PassengerRepository passengerRepository;

    public AlertService(AlertRepository alertRepository, PassengerRepository passengerRepository) {
        this.alertRepository = alertRepository;
        this.passengerRepository = passengerRepository;
    }

    public Alert createAlertForPassenger(Passenger passenger, Flight flight, Date alertDate, String message, String severity) {
        Alert existingAlert = alertRepository.findByPassengerAndFlightAndMessageAndAlertDate(passenger, flight, message, alertDate);
        if (existingAlert != null) {
            return existingAlert;
        }
        Severity severityEnum;
        try {
            severityEnum = Severity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid severity value", e);
        }
        Alert alert = new Alert();
        alert.setPassenger(passenger);
        alert.setFlight(flight);
        alert.setMessage(message);
        alert.setSeverity(severityEnum);
        alert.setAlertDate(alertDate);
        return alertRepository.save(alert);
    }


    public List<Alert> getAlertsForPassenger(Long passengerId) {
        return alertRepository.findByPassengerId(passengerId);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }
}
