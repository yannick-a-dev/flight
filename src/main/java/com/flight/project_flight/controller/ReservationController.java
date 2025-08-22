package com.flight.project_flight.controller;

import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/flights/")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("{flightNumber}/reservations")
    public List<Reservation> getReservations(@PathVariable String flightNumber) {
        return reservationService.getReservationsByFlightNumber(flightNumber);
    }

    @PostMapping("{flightNumber}/reservations")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable String flightNumber,
            @RequestBody ReservationDto dto) {
        dto.setFlightNumber(flightNumber);

        Reservation reservation = reservationService.createReservation(dto);
        return ResponseEntity.ok(reservation);
    }
}

