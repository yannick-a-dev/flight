package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertResponseDto;
import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.dto.FlightResponseDto;
import com.flight.project_flight.dto.ReservationResponseDto;
import com.flight.project_flight.enums.FlightStatus;
import com.flight.project_flight.exception.AirportNotFoundException;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.AirportRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    private final ReservationMapper reservationMapper;
    private final AlertMapper alertMapper;
    private final AirportRepository airportRepository;

    public FlightMapper(ReservationMapper reservationMapper,
                        AlertMapper alertMapper,
                        AirportRepository airportRepository) {
        this.reservationMapper = reservationMapper;
        this.alertMapper = alertMapper;
        this.airportRepository = airportRepository;
    }

    public Flight toEntity(FlightDto dto) {
        Flight flight = new Flight();

        flight.setFlightNumber(dto.getFlightNumber());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setStatus(FlightStatus.valueOf(dto.getStatus()));
        return flight;
    }

    public FlightResponseDto toResponseDto(Flight flight) {
        FlightResponseDto dto = new FlightResponseDto();

        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDepartureAirport(getCode(flight.getDepartureAirport()));
        dto.setArrivalAirport(getCode(flight.getArrivalAirport()));
        dto.setStatus(flight.getStatus().name());

        // ✅ plus de duplication
        dto.setReservations(mapReservations(flight.getReservations()));
        dto.setAlerts(mapAlerts(flight.getAlerts()));

        return dto;
    }

    private Airport findAirport(String code) {
        return airportRepository.findByCode(code)
                .orElseThrow(() -> new AirportNotFoundException(code));
    }

    private String getCode(Airport airport) {
        return airport != null ? airport.getCode() : null;
    }

    private List<ReservationResponseDto> mapReservations(List<Reservation> reservations) {
        if (reservations == null) return Collections.emptyList();

        return reservations.stream()
                .map(reservationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private List<AlertResponseDto> mapAlerts(List<Alert> alerts) {
        if (alerts == null) return Collections.emptyList();

        return alerts.stream()
                .map(alertMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}