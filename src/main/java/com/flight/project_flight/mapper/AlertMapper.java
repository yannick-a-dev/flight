package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.PassengerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertMapper {

    private final PassengerService passengerService;

    public AlertMapper(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public Alert toEntity(AlertDto alertDto) {
        Alert alert = new Alert();
        alert.setMessage(alertDto.getMessage());
        alert.setAlertDate(alertDto.getAlertDate());
        alert.setSeverity(Severity.valueOf(alertDto.getSeverity().toUpperCase()));
        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        alert.setPassenger(passenger);
        return alert;
    }

    public AlertDto toDto(Alert alert) {
        AlertDto alertDto = new AlertDto();
        alertDto.setMessage(alert.getMessage());
        alertDto.setAlertDate(alert.getAlertDate());
        alertDto.setSeverity(alert.getSeverity().name().toLowerCase());
        alertDto.setPassengerId(alert.getPassenger().getId());
        return alertDto;
    }

    public List<Alert> mapToAlerts(List<AlertDto> alertDtos, Flight flight) {
        return alertDtos.stream()
                .map(dto -> {
                    Alert alert = new Alert();
                    alert.setMessage(dto.getMessage());
                    Severity severity = Severity.valueOf(dto.getSeverity());
                    alert.setSeverity(severity);
                    alert.setFlight(flight);
                    return alert;
                })
                .collect(Collectors.toList());
    }
}
