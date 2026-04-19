package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.dto.ReservationResponseDto;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.service.PassengerService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    private final PassengerService passengerService;

    public ReservationMapper(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    // =========================
    // DTO -> ENTITY
    // =========================
    public Reservation toEntity(ReservationDto dto) {
        if (dto == null) return null;

        Reservation reservation = new Reservation();

        reservation.setId(dto.getId());
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setSeatNumber(dto.getSeatNumber());
        reservation.setPrice(dto.getPrice());

        if (dto.getPassengerId() != null) {
            reservation.setPassenger(passengerService.findById(dto.getPassengerId()));
        }

        return reservation;
    }

    // =========================
    // ENTITY -> DTO
    // =========================
    public ReservationDto toDto(Reservation reservation) {
        if (reservation == null) return null;

        ReservationDto dto = new ReservationDto();

        dto.setId(reservation.getId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setSeatNumber(reservation.getSeatNumber());
        dto.setPrice(reservation.getPrice());

        if (reservation.getPassenger() != null) {
            dto.setPassengerId(reservation.getPassenger().getId());
        }

        if (reservation.getFlight() != null) {
            dto.setFlightNumber(reservation.getFlight().getFlightNumber());
        }

        return dto;
    }

    // =========================
    // LIST MAPPING (PROPRE)
    // =========================
    public List<Reservation> toEntityList(List<ReservationDto> dtos, Flight flight) {
        if (dtos == null) return Collections.emptyList();

        return dtos.stream()
                .map(dto -> {
                    Reservation reservation = toEntity(dto); // ✅ réutilisation
                    reservation.setFlight(flight);           // 🔥 lien important
                    return reservation;
                })
                .collect(Collectors.toList());
    }

    public List<ReservationDto> toDtoList(List<Reservation> reservations) {
        if (reservations == null) return Collections.emptyList();

        return reservations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReservationResponseDto toResponseDto(Reservation reservation) {
        if (reservation == null) return null;

        ReservationResponseDto dto = new ReservationResponseDto();

        dto.setId(reservation.getId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setSeatNumber(reservation.getSeatNumber());
        dto.setPrice(reservation.getPrice() != null ? reservation.getPrice().doubleValue() : null);

        if (reservation.getPassenger() != null) {
            dto.setPassengerId(reservation.getPassenger().getId());
        }

        return dto;
    }

    public List<ReservationResponseDto> toResponseDtoList(List<Reservation> reservations) {
        if (reservations == null) return Collections.emptyList();

        return reservations.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}