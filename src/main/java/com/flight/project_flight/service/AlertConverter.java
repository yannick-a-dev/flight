package com.flight.project_flight.service;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import org.springframework.stereotype.Service;

@Service
public class AlertConverter {

    public AlertDto convertToDto(Alert alert) {
        AlertDto alertDto = new AlertDto();
        alertDto.setMessage(alert.getMessage());
        alertDto.setAlertDate(alert.getAlertDate());
        alertDto.setSeverity(alert.getSeverity().name()); // Conversion de l'énumération en chaîne
        alertDto.setPassengerId(alert.getPassenger().getId());
        alertDto.setFlightId(alert.getFlight().getId());
        return alertDto;
    }

    public Alert convertToEntity(AlertDto alertDto, Passenger passenger, Flight flight) {
        Alert alert = new Alert();
        alert.setMessage(alertDto.getMessage());
        alert.setAlertDate(alertDto.getAlertDate());
        alert.setSeverity(Severity.valueOf(alertDto.getSeverity())); // Conversion de la chaîne en énumération
        alert.setPassenger(passenger);
        alert.setFlight(flight);
        return alert;
    }
}

