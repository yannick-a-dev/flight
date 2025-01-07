package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.exception.PassengerNotFoundException;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.service.PassengerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {
    private final PassengerRepository passengerRepository;

    private final PassengerService passengerService;

    public ReservationMapper(PassengerRepository passengerRepository, PassengerService passengerService) {
        this.passengerRepository = passengerRepository;
        this.passengerService = passengerService;
    }

    public Reservation toEntity(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setReservationDate(reservationDto.getReservationDate());
        reservation.setSeatNumber(reservationDto.getSeatNumber());
        reservation.setPrice(reservationDto.getPrice());
        Passenger passenger = passengerService.findById(reservationDto.getPassengerId());
        reservation.setPassenger(passenger);
        return reservation;
    }

    public ReservationDto toDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setReservationDate(reservation.getReservationDate());
        reservationDto.setSeatNumber(reservation.getSeatNumber());
        reservationDto.setPrice(reservation.getPrice());
        reservationDto.setPassengerId(reservation.getPassenger().getId());
        return reservationDto;
    }

    public List<Reservation> mapToReservations(List<ReservationDto> reservationDtos, Flight flight) {
        return reservationDtos.stream()
                .map(dto -> {
                    Reservation reservation = new Reservation();
                    reservation.setId(dto.getId());
                    reservation.setReservationDate(dto.getReservationDate());
                    reservation.setSeatNumber(dto.getSeatNumber());
                    reservation.setPrice(dto.getPrice());
                    // Extraire le Passenger de l'Optional
                    Passenger passenger = passengerRepository.findById(dto.getPassengerId())
                            .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + dto.getPassengerId()));
                    reservation.setPassenger(passenger);
                    reservation.setFlight(flight);
                    return reservation;
                })
                .collect(Collectors.toList());
    }
}
