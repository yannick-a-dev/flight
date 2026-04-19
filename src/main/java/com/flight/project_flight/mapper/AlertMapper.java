package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.AlertResponseDto;
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

    public AlertMapper(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    // =========================
    // DTO -> ENTITY
    // =========================
    public Alert toEntity(AlertDto dto) {
        if (dto == null) return null;

        Alert alert = new Alert();

        alert.setMessage(dto.getMessage());
        alert.setAlertDate(dto.getAlertDate());
        alert.setSeverity(parseSeverity(dto.getSeverity()));

        if (dto.getPassengerId() != null) {
            alert.setPassenger(passengerService.findById(dto.getPassengerId()));
        }

        return alert;
    }

    // =========================
    // ENTITY -> RESPONSE DTO
    // =========================
    public AlertResponseDto toResponseDto(Alert alert) {
        if (alert == null) return null;

        AlertResponseDto dto = new AlertResponseDto();

        dto.setMessage(alert.getMessage());
        dto.setAlertDate(alert.getAlertDate());
        dto.setSeverity(alert.getSeverity() != null ? alert.getSeverity().name() : null);

        if (alert.getPassenger() != null) {
            dto.setPassengerId(alert.getPassenger().getId());
        }

        return dto;
    }

    // =========================
    // LIST MAPPING
    // =========================
    public List<Alert> toEntityList(List<AlertDto> dtos, Flight flight) {
        if (dtos == null) return Collections.emptyList();

        return dtos.stream()
                .map(dto -> {
                    Alert alert = toEntity(dto); // ✅ réutilisation
                    alert.setFlight(flight);     // 🔥 relation JPA
                    return alert;
                })
                .collect(Collectors.toList());
    }

    public List<AlertResponseDto> toResponseDtoList(List<Alert> alerts) {
        if (alerts == null) return Collections.emptyList();

        return alerts.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // =========================
    // HELPER
    // =========================
    private Severity parseSeverity(String severity) {
        if (severity == null) return null;

        try {
            return Severity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid severity value: {}", severity);
            return null; // ou Severity.LOW si tu veux une valeur par défaut
        }
    }
}