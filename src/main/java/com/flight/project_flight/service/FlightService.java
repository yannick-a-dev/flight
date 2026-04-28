package com.flight.project_flight.service;

import com.flight.project_flight.dto.*;
import com.flight.project_flight.enums.FlightStatus;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.exception.AirportNotFoundException;
import com.flight.project_flight.exception.FlightNotFoundException;
import com.flight.project_flight.exception.InvalidFlightDataException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.mapper.FlightMapper;
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
    private final AirportRepository airportRepository;
    private final FlightMapper flightMapper;

    public FlightService(FlightRepository flightRepository,
                         AlertMapper alertMapper,
                         ReservationMapper reservationMapper,
                         AirportRepository airportRepository, FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.alertMapper = alertMapper;
        this.reservationMapper = reservationMapper;
        this.airportRepository = airportRepository;
        this.flightMapper = flightMapper;
    }

    public Flight createFlight(FlightDto dto) {
        if (flightRepository.findByFlightNumber(dto.getFlightNumber()).isPresent()) {
            throw new IllegalArgumentException("Flight already exists");
        }

        Airport departure = airportRepository.findByCode(dto.getDepartureAirport())
                .orElseThrow(() -> new AirportNotFoundException(dto.getDepartureAirport()));

        Airport arrival = airportRepository.findByCode(dto.getArrivalAirport())
                .orElseThrow(() -> new AirportNotFoundException(dto.getArrivalAirport()));

        Flight flight = flightMapper.toEntity(dto);
        flight.setDepartureAirport(departure);
        flight.setArrivalAirport(arrival);

        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlight(String flightNumber, FlightDto dto) {

        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));

        validateDates(dto);
        applyBaseFields(flight, dto);
        applyAirports(flight, dto);
        applyStatus(flight, dto.getStatus());
        refreshRelations(flight);
        return flightRepository.save(flight);
    }

    private void applyBaseFields(Flight flight, FlightDto dto) {
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
    }

    private void applyAirports(Flight flight, FlightDto dto) {
        flight.setDepartureAirport(findAirport(dto.getDepartureAirport(), "Departure"));
        flight.setArrivalAirport(findAirport(dto.getArrivalAirport(), "Arrival"));
    }
    private void applyStatus(Flight flight, String status) {
        try {
            flight.setStatus(FlightStatus.valueOf(status));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid flight status: " + status);
        }
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));
    }

    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    public List<Flight> getFlightsByPassenger(Long passengerId) {
        return flightRepository.findFlightsByPassengerId(passengerId);
    }

    public long countAllFlights() {
        return flightRepository.count();
    }

    public void deleteFlight(String flightNumber) {
        Flight flight = getFlightByNumber(flightNumber);
        flightRepository.delete(flight);
    }

    public List<FlightResponseDto> getFlightsByAirport(Long airportId) {
        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found"));
        String code = airport.getCode();
        return flightRepository
                .findByDepartureAirport_CodeOrArrivalAirport_Code(code, code)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private FlightResponseDto toDto(Flight flight) {

        List<ReservationResponseDto> reservations =
                reservationMapper.toResponseDtoList(flight.getReservations());

        List<AlertResponseDto> alerts =
                alertMapper.toResponseDtoList(flight.getAlerts());

        return buildDto(flight, reservations, alerts);
    }

    private static FlightResponseDto buildDto(
            Flight flight,
            List<ReservationResponseDto> reservations,
            List<AlertResponseDto> alerts) {

        FlightResponseDto dto = new FlightResponseDto();

        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDepartureAirport(getCode(flight.getDepartureAirport()));
        dto.setArrivalAirport(getCode(flight.getArrivalAirport()));
        dto.setStatus(flight.getStatus().name());

        dto.setReservations(reservations);
        dto.setAlerts(alerts);

        return dto;
    }

    private static String getCode(Airport airport) {
        return airport != null ? airport.getCode() : null;
    }


    private Airport findAirport(String code, String label) {
        return airportRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(label + " airport not found: " + code));
    }

    private void refreshRelations(Flight flight) {

        flight.getAlerts().clear();
        flight.getReservations().clear();
    }

    private void validateDates(FlightDto dto) {
        if (dto.getDepartureTime().isAfter(dto.getArrivalTime())) {
            throw new InvalidFlightDataException("Departure after arrival");
        }
    }
}
