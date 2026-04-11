package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Ticket;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.repository.TicketRepository;
import com.flight.project_flight.service.PassengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlertMapper {
    private final PassengerService passengerService;
    private final PassengerRepository passengerRepository;

    public AlertMapper(PassengerService passengerService, PassengerRepository passengerRepository) {
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
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
        if (alertDtos == null) return Collections.emptyList();

        return alertDtos.stream()
                .map(dto -> {
                    Alert alert = new Alert();
                    alert.setMessage(dto.getMessage());
                    alert.setAlertDate(dto.getAlertDate());
                    alert.setSeverity(Severity.valueOf(dto.getSeverity()));
                    alert.setFlight(flight);

                    if (dto.getPassengerId() != null) {
                        passengerRepository.findById(dto.getPassengerId()).ifPresent(alert::setPassenger);
                        // si Passenger absent, on ignore au lieu de lancer exception
                    }

                    return alert;
                })
                .collect(Collectors.toList());
    }

    public AlertDto mapToDto(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setMessage(alert.getMessage());
        dto.setAlertDate(alert.getAlertDate() != null ? alert.getAlertDate() : LocalDateTime.now());
        dto.setSeverity(alert.getSeverity() != null ? alert.getSeverity().name() : "LOW");
        dto.setPassengerId(alert.getPassenger() != null ? alert.getPassenger().getId() : null);
        dto.setFlightNumber(alert.getFlight() != null ? alert.getFlight().getFlightNumber() : null);
        return dto;
    }
}
