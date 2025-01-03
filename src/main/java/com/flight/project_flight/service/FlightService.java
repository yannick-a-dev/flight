package com.flight.project_flight.service;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.FlightDto;
import com.flight.project_flight.dto.ReservationDto;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Reservation;
import com.flight.project_flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private static final PassengerService passengerService = null;
    private static final FlightService flightService = null;
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight createFlight(FlightDto flightDto) {
        // Validation (exemple de vérifier si un vol avec le même numéro existe déjà)
        Optional<Flight> existingFlight = flightRepository.findByFlightNumber(flightDto.getFlightNumber());
        if (existingFlight.isPresent()) {
            throw new IllegalArgumentException("Flight with this flight number already exists.");
        }

        // Mapper le DTO à l'entité Flight
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setStatus(flightDto.getStatus());
        // Convertir les réservations
        List<Reservation> reservations = flightDto.getReservations().stream()
                .map(reservationDto -> toEntity(reservationDto)) // Convertir chaque ReservationDto en Reservation
                .collect(Collectors.toList());
        flight.setReservations(reservations);

        // Convertir les alertes
        List<Alert> alerts = flightDto.getAlerts().stream()
                .map(alertDto -> toEntity(alertDto)) // Convertir chaque AlertDto en Alert
                .collect(Collectors.toList());
        flight.setAlerts(alerts);

        // Sauvegarder le vol dans la base de données
        return flightRepository.save(flight);
    }

    private static Reservation toEntity(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.getReservationDate(reservationDto.getReservationDate());
        reservation.setSeatNumber(reservationDto.getSeatNumber());
        reservation.setPrice(reservationDto.getPrice());
        Passenger passenger = passengerService.findById(reservationDto.getPassengerId());
        reservation.setPassenger(passenger);
        Flight flight = flightService.findById(reservationDto.getFlightId());
        reservation.setFlight(flight);
        return reservation;
    }

    private static Alert toEntity(AlertDto alertDto) {
        Alert alert = new Alert();
        alert.setMessage(alertDto.getMessage());
        alert.setAlertDate(alertDto.getAlertDate());
        Severity severity = Severity.valueOf(alertDto.getSeverity().toUpperCase());  // Utilise `toUpperCase()` pour éviter les problèmes de casse
        alert.setSeverity(severity);
        Passenger passenger = passengerService.findById(alertDto.getPassengerId());
        alert.setPassenger(passenger);
        Flight flight = flightService.findById(alertDto.getFlightId());
        alert.setFlight(flight);
        // Assurez-vous d'assigner le vol à partir du DTO si nécessaire
        return alert;
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Flight not found"));
    }
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Flight updateFlight(Long id, Flight flightDetails) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id " + id));

        flight.setFlightNumber(flightDetails.getFlightNumber());
        flight.setDepartureTime(flightDetails.getDepartureTime());
        flight.setArrivalTime(flightDetails.getArrivalTime());
        flight.setDepartureAirport(flightDetails.getDepartureAirport());
        flight.setArrivalAirport(flightDetails.getArrivalAirport());
        flight.setStatus(flightDetails.getStatus());
        flight.setReservations(flightDetails.getReservations());
        flight.setAlerts(flightDetails.getAlerts());

        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id " + id));
        flightRepository.delete(flight);
    }
    public List<Flight> getFlightsByPassenger(Long passengerId) {
        return flightRepository.findFlightsByPassengerId(passengerId);
    }
}
