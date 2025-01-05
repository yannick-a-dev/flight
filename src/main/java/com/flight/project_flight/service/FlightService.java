package com.flight.project_flight.service;

import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.mapper.ReservationMapper;
import com.flight.project_flight.models.Flight;
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

    public FlightService(FlightRepository flightRepository, AlertMapper alertMapper, ReservationMapper reservationMapper, PassengerService passengerService) {
        this.flightRepository = flightRepository;
        this.alertMapper = alertMapper;
        this.reservationMapper = reservationMapper;
        this.passengerService = passengerService;
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
        flight.setStatus(flightDto.getStatus());
        flight.setReservations(flightDto.getReservations().stream()
                .map(reservationMapper::toEntity)
                .collect(Collectors.toList()));
        flight.setAlerts(flightDto.getAlerts().stream()
                .map(alertMapper::toEntity)
                .collect(Collectors.toList()));

        return flightRepository.save(flight);
    }

    public Flight findByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber).orElseThrow(() -> new RuntimeException("Flight not found"));
    }
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    public Flight updateFlight(String flightNumber, Flight flightDetails) {
        // Find the flight by flightNumber
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));

        // Validate flight data (example)
        if (flightDetails.getDepartureTime().isAfter(flightDetails.getArrivalTime())) {
            throw new InvalidFlightDataException("Departure time cannot be after arrival time.");
        }

        // Update flight details
        flight.setFlightNumber(flightDetails.getFlightNumber());
        flight.setDepartureTime(flightDetails.getDepartureTime());
        flight.setArrivalTime(flightDetails.getArrivalTime());
        flight.setDepartureAirport(flightDetails.getDepartureAirport());
        flight.setArrivalAirport(flightDetails.getArrivalAirport());
        flight.setStatus(flightDetails.getStatus());
        flight.setReservations(flightDetails.getReservations());
        flight.setAlerts(flightDetails.getAlerts());

        // Save and return the updated flight
        return flightRepository.save(flight);
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
