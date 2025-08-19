package com.flight.project_flight.service;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.enums.FlightStatus;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.mapper.ReservationMapper;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Reservation;
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
        existingFlight.setStatus(FlightStatus.valueOf(flightDto.getStatus()));
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

    public Flight getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with number: " + flightNumber));
    }

}
