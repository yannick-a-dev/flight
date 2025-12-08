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
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.AirportRepository;
import com.flight.project_flight.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        if (flightRepository.findByFlightNumber(flightDto.getFlightNumber()).isPresent()) {
            throw new IllegalArgumentException("Flight with this flight number already exists.");
        }

        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());

        try {
            flight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid flight status: " + flightDto.getStatus());
        }

        // --- ALERTS ---
        if (flightDto.getAlerts() != null) {
            List<Alert> alerts = alertMapper.mapToAlerts(flightDto.getAlerts(), flight);
            for (Alert alert : alerts) {
                flight.addAlert(alert); // garde la même instance de collection
            }
        }

        // --- RESERVATIONS ---
        if (flightDto.getReservations() != null) {
            List<Reservation> reservations = reservationMapper.mapToReservations(flightDto.getReservations(), flight);
            for (Reservation reservation : reservations) {
                flight.addReservation(reservation);
            }
        }

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

    public Flight updateFlight(String flightNumber, FlightDto flightDto) {
        // 1. Find the existing flight by its flight number
        Flight existingFlight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));

        // 2. Validation des dates
        if (flightDto.getDepartureTime().isAfter(flightDto.getArrivalTime())) {
            throw new InvalidFlightDataException("Departure time cannot be after arrival time.");
        }

        // 3. Mise à jour des informations de base
        existingFlight.setDepartureTime(flightDto.getDepartureTime());
        existingFlight.setArrivalTime(flightDto.getArrivalTime());
        existingFlight.setDepartureAirport(flightDto.getDepartureAirport());
        existingFlight.setArrivalAirport(flightDto.getArrivalAirport());

        try {
            existingFlight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new InvalidFlightDataException("Invalid flight status: " + flightDto.getStatus());
        }

        // --- ALERTS ---
        List<Alert> existingAlerts = existingFlight.getAlerts();
        Iterator<Alert> alertIterator = existingAlerts.iterator();
        while (alertIterator.hasNext()) {
            Alert alert = alertIterator.next();
            alertIterator.remove();   // supprime de la collection
            alert.setFlight(null);    // rompt la relation
        }

        if (flightDto.getAlerts() != null) {
            List<Alert> mappedAlerts = alertMapper.mapToAlerts(flightDto.getAlerts(), existingFlight);
            for (Alert alert : mappedAlerts) {
                existingFlight.addAlert(alert);
            }
        }

        // --- RESERVATIONS ---
        List<Reservation> existingReservations = existingFlight.getReservations();
        Iterator<Reservation> resIterator = existingReservations.iterator();
        while (resIterator.hasNext()) {
            Reservation res = resIterator.next();
            resIterator.remove();
            res.setFlight(null);
        }

        if (flightDto.getReservations() != null) {
            List<Reservation> mappedReservations = reservationMapper.mapToReservations(flightDto.getReservations(), existingFlight);
            for (Reservation res : mappedReservations) {
                existingFlight.addReservation(res);
            }
        }
        // 4. Sauvegarde
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

    public List<FlightDto> getFlightsByAirport(Long airportId) {
        // 1️⃣ Récupérer l'aéroport pour obtenir son code IATA
        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + airportId));

        String airportCode = airport.getCode();

        // 2️⃣ Récupérer les vols où l'aéroport est départ ou arrivée
        List<Flight> flights = flightRepository.findByDepartureAirportOrArrivalAirport(airportCode, airportCode);

        // 3️⃣ Mapper les vols en FlightDto
        return flights.stream()
                .map(flight -> {
                    List<ReservationDto> reservations = flight.getReservations().stream()
                            .map(reservationMapper::toDto)
                            .collect(Collectors.toList());

                    List<AlertDto> alerts = flight.getAlerts().stream()
                            .map(alertMapper::toDto)
                            .collect(Collectors.toList());

                    var dto = getFlightDto(flight, reservations, alerts);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private static FlightDto getFlightDto(Flight flight, List<ReservationDto> reservations, List<AlertDto> alerts) {
        FlightDto dto = new FlightDto();
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDepartureAirport(flight.getDepartureAirport());
        dto.setArrivalAirport(flight.getArrivalAirport());
        dto.setStatus(flight.getStatus().name());
        dto.setReservations(reservations);
        dto.setAlerts(alerts);
        return dto;
    }

    public long countAllFlights() {
        return flightRepository.count();
    }
}
