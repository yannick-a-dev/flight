package com.flight.project_flight.service;

import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.PassengerNotFoundException;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.FlightRepository;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    public ReservationService(ReservationRepository reservationRepository, FlightRepository flightRepository, PassengerRepository passengerRepository) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    @Transactional
    public Reservation createReservation(ReservationDto dto) {
        // Vérifier que le vol existe
        Flight flight = flightRepository.findByFlightNumber(dto.getFlightNumber())
                .orElseThrow(() -> new FlightNotFoundException(dto.getFlightNumber()));

        // Vérifier que le passager existe
        Passenger passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException(dto.getPassengerId()));

        // Créer l'entité Reservation
        Reservation reservation = new Reservation();
        reservation.setFlight(flight);
        reservation.setPassenger(passenger);
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setSeatNumber(dto.getSeatNumber());
        reservation.setPrice(dto.getPrice());
        flight.getReservations().add(reservation);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByFlightNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return flight.getReservations();
    }
    @Transactional
    public void deleteReservationById(Long id) {
        // Vérifie si la réservation existe avant de supprimer
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Reservation with id " + id + " not found");
        }
    }
}

