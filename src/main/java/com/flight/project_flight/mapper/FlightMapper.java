package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertResponseDto;
import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.dto.FlightResponseDto;
import com.flight.project_flight.dto.ReservationResponseDto;
import com.flight.project_flight.enums.FlightStatus;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    private final ReservationMapper reservationMapper;
    private final AlertMapper alertMapper;

    public FlightMapper(ReservationMapper reservationMapper, AlertMapper alertMapper) {
        this.reservationMapper = reservationMapper;
        this.alertMapper = alertMapper;
    }

    public Flight toEntity(FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));

        if (flightDto.getReservations() != null && !flightDto.getReservations().isEmpty()) {
            List<Reservation> reservations = reservationMapper.mapToReservations(flightDto.getReservations(), flight);
            reservations.forEach(flight::addReservation);
        }
        if (flightDto.getAlerts() != null && !flightDto.getAlerts().isEmpty()) {
            List<Alert> alerts = alertMapper.mapToAlerts(flightDto.getAlerts(), flight);
            alerts.forEach(flight::addAlert);
        }

        return flight;
    }

    public FlightResponseDto toResponseDto(Flight flight) {
        FlightResponseDto dto = new FlightResponseDto();
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDepartureAirport(flight.getDepartureAirport());
        dto.setArrivalAirport(flight.getArrivalAirport());
        dto.setStatus(flight.getStatus().name());

        // Map reservations
        List<ReservationResponseDto> reservationDtos = flight.getReservations() != null ?
                flight.getReservations().stream().map(res -> {
                    ReservationResponseDto rDto = new ReservationResponseDto();
                    rDto.setId(res.getId());
                    rDto.setReservationDate(res.getReservationDate());
                    rDto.setSeatNumber(res.getSeatNumber());
                    rDto.setPrice(res.getPrice() != null ? res.getPrice().doubleValue() : null);
                    rDto.setPassengerId(res.getPassenger() != null ? res.getPassenger().getId() : null);
                    return rDto;
                }).collect(Collectors.toList()) : new ArrayList<>();

        dto.setReservations(reservationDtos);

        // Map alerts
        List<AlertResponseDto> alertDtos = flight.getAlerts() != null ?
                flight.getAlerts().stream().map(alert -> {
                    AlertResponseDto aDto = new AlertResponseDto();
                    aDto.setMessage(alert.getMessage());
                    aDto.setAlertDate(alert.getAlertDate());
                    aDto.setSeverity(alert.getSeverity() != null ? alert.getSeverity().name() : null);
                    aDto.setPassengerId(alert.getPassenger() != null ? alert.getPassenger().getId() : null);
                    return aDto;
                }).collect(Collectors.toList()) : new ArrayList<>();

        dto.setAlerts(alertDtos);

        return dto;
    }


}
