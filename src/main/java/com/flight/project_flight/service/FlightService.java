package com.flight.project_flight.service;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.enums.FlightStatus;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.mapper.ReservationMapper;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.AirportRepository;
import com.flight.project_flight.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AlertMapper alertMapper;
    private final ReservationMapper reservationMapper;
    private final PassengerService passengerService;

    private final AirportRepository airportRepository;

    public FlightService(FlightRepository flightRepository, AlertMapper alertMapper, ReservationMapper reservationMapper, PassengerService passengerService, AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.alertMapper = alertMapper;
        this.reservationMapper = reservationMapper;
        this.passengerService = passengerService;
        this.airportRepository = airportRepository;
    }


    public Flight createFlight(FlightDto flightDto) {
        // Vérifier si le vol existe déjà
        if (flightRepository.findByFlightNumber(flightDto.getFlightNumber()).isPresent()) {
            throw new IllegalArgumentException("Flight with this flight number already exists.");
        }

        // Créer le vol
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());

        // Conversion sécurisée du status
        try {
            flight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid flight status: " + flightDto.getStatus());
        }

        // MAPPING des réservations (avec lien Flight)
        if (flightDto.getReservations() != null) {
            List<Reservation> reservations = reservationMapper.mapToReservations(flightDto.getReservations(), flight);
            flight.setReservations(reservations);
        }

        // MAPPING des alertes
        if (flightDto.getAlerts() != null) {
            List<Alert> alerts = alertMapper.mapToAlerts(flightDto.getAlerts(), flight);
            flight.setAlerts(alerts);
        }

        // Sauvegarde finale
        return flightRepository.save(flight);
    }

    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    @Transactional
    public Flight updateFlight(String flightNumber, FlightDto flightDto) {
        Flight existingFlight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));

        // Validation des dates
        if (flightDto.getDepartureTime().isAfter(flightDto.getArrivalTime())) {
            throw new InvalidFlightDataException("Departure time cannot be after arrival time.");
        }

        existingFlight.setDepartureTime(flightDto.getDepartureTime());
        existingFlight.setArrivalTime(flightDto.getArrivalTime());
        existingFlight.setDepartureAirport(flightDto.getDepartureAirport());
        existingFlight.setArrivalAirport(flightDto.getArrivalAirport());

        // Validation du statut
        try {
            existingFlight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new InvalidFlightDataException("Invalid flight status: " + flightDto.getStatus());
        }

        // --- ALERTS ---
        // Supprimer les alertes existantes non présentes dans le DTO
        existingFlight.getAlerts().removeIf(alert ->
                flightDto.getAlerts() == null || flightDto.getAlerts().stream().noneMatch(dto ->
                        dto.getId() != null && dto.getId().equals(alert.getId())
                )
        );

        if (flightDto.getAlerts() != null) {
            for (AlertDto dto : flightDto.getAlerts()) {
                if (dto.getId() == null) {
                    Alert newAlert = alertMapper.toEntity(dto);
                    newAlert.setFlight(existingFlight);
                    existingFlight.getAlerts().add(newAlert);
                } else {
                    existingFlight.getAlerts().stream()
                            .filter(a -> a.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(alert -> {
                                alert.setMessage(dto.getMessage());
                                alert.setAlertDate(dto.getAlertDate());
                                alert.setSeverity(Severity.valueOf(dto.getSeverity()));
                            });
                }
            }
        }

        // --- RESERVATIONS ---
        existingFlight.getReservations().removeIf(reservation ->
                flightDto.getReservations() == null || flightDto.getReservations().stream().noneMatch(dto ->
                        dto.getId() != null && dto.getId().equals(reservation.getId())
                )
        );

        if (flightDto.getReservations() != null) {
            for (ReservationDto dto : flightDto.getReservations()) {
                if (dto.getId() == null) {
                    Reservation newRes = reservationMapper.mapToReservations(List.of(dto), existingFlight).get(0);
                    existingFlight.getReservations().add(newRes);
                } else {
                    existingFlight.getReservations().stream()
                            .filter(r -> r.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(reservation -> {
                                reservation.setSeatNumber(dto.getSeatNumber());
                                reservation.setPrice(dto.getPrice());
                                reservation.setReservationDate(dto.getReservationDate());
                            });
                }
            }
        }

        return flightRepository.save(existingFlight);
    }


    public void deleteFlight(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found with id " + flightNumber));
        flightRepository.delete(flight);
    }
    public List<Flight> getFlightsByPassenger(Long passengerId) {
        return flightRepository.findFlightsByPassengerId(passengerId);
    }

    public Flight getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with number: " + flightNumber));
    }

}
