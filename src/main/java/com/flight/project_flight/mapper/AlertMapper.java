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
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlertMapper {

    private final TicketRepository ticketRepository;
    private final PassengerService passengerService;
    private final PassengerRepository passengerRepository;

    public AlertMapper(TicketRepository ticketRepository, PassengerService passengerService, PassengerRepository passengerRepository) {
        this.ticketRepository = ticketRepository;
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

        return alertDtos.stream()
                .map(dto -> {
                    Alert alert = new Alert();
                    alert.setMessage(dto.getMessage());
                    alert.setAlertDate(dto.getAlertDate());
                    alert.setSeverity(Severity.valueOf(dto.getSeverity()));

                    // Flight attaché
                    alert.setFlight(flight);

                    // Passenger attaché si présent
                    if (dto.getPassengerId() != null) {
                        Passenger passenger = passengerRepository.findById(dto.getPassengerId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Passenger not found with ID: " + dto.getPassengerId()));
                        alert.setPassenger(passenger);
                    }

                    // Ticket attaché si présent (ne pas toucher ticket.getAlerts())
//                    if (dto.getTicketNumber() != null) {
//                        Ticket ticket = ticketRepository.findById(dto.getTicketNumber())
//                                .orElseThrow(() -> new RuntimeException(
//                                        "Ticket not found with number: " + dto.getTicketNumber()));
//                        alert.setTicket(ticket);
//                    }

                    return alert;
                })
                .collect(Collectors.toList());
    }

    public AlertDto mapToDto(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setMessage(alert.getMessage());
        dto.setAlertDate(alert.getAlertDate() != null ? alert.getAlertDate() : LocalDateTime.now()); // ✅ fallback
        dto.setSeverity(alert.getSeverity() != null ? alert.getSeverity().name() : "LOW"); // ✅ fallback
        dto.setPassengerId(alert.getPassenger() != null ? alert.getPassenger().getId() : null);
        dto.setFlightNumber(alert.getFlight() != null ? alert.getFlight().getFlightNumber() : null);
        return dto;
    }
}
