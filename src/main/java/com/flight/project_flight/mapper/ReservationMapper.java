package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.service.PassengerService;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final PassengerService passengerService;

    public ReservationMapper(PassengerService passengerService) {
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
}
