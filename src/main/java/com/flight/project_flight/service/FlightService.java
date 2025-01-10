package com.flight.project_flight.service;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.mapper.ReservationMapper;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.repository.AirportRepository;
import com.flight.project_flight.repository.FlightRepository;
import org.springframework.stereotype.Service;

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
        // Vérifier si un vol avec le même numéro existe déjà
        if (flightRepository.findByFlightNumber(flightDto.getFlightNumber()).isPresent()) {
            throw new IllegalArgumentException("Flight with this flight number already exists.");
        }

        // Créer un nouvel objet Flight
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());

        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());

        // Définir le statut du vol
        flight.setStatus(flightDto.getStatus());

        // Mapper les réservations et alertes depuis les DTOs
        flight.setReservations(flightDto.getReservations().stream()
                .map(reservationMapper::toEntity)
                .collect(Collectors.toList()));
        flight.setAlerts(flightDto.getAlerts().stream()
                .map(alertMapper::toEntity)
                .collect(Collectors.toList()));

        // Sauvegarder le vol
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

        // 2. Validate the flight data
        if (flightDto.getDepartureTime().isAfter(flightDto.getArrivalTime())) {
            throw new InvalidFlightDataException("Departure time cannot be after arrival time.");
        }

        // 3. Map DTO to entity
        existingFlight.setDepartureTime(flightDto.getDepartureTime());
        existingFlight.setArrivalTime(flightDto.getArrivalTime());

        existingFlight.setDepartureAirport(flightDto.getDepartureAirport());
        existingFlight.setArrivalAirport(flightDto.getArrivalAirport());
        // Update other fields
        existingFlight.setStatus(flightDto.getStatus());
        existingFlight.setReservations(reservationMapper.mapToReservations(flightDto.getReservations(), existingFlight));
        existingFlight.setAlerts(alertMapper.mapToAlerts(flightDto.getAlerts(), existingFlight));

        // 4. Save and return the updated flight
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
}
