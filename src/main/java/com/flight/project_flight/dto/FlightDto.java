package com.flight.project_flight.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flight.project_flight.config.CustomLocalDateTimeDeserializer;
import com.flight.project_flight.enums.FlightStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class FlightDto {
    @NotNull(message = "Flight number cannot be null")
    private String flightNumber;

    @NotNull(message = "Departure date cannot be null")
    @PastOrPresent(message = "Departure date must be in the past or present")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival date cannot be null")
    @Future(message = "Arrival date must be in the future")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime arrivalTime;

    @NotNull(message = "Departure airport cannot be null")
    private AirportDTO departureAirport;

    @NotNull(message = "Arrival airport cannot be null")
    private AirportDTO arrivalAirport;

    @NotNull(message = "Status cannot be null")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private FlightStatus status;

    @NotNull(message = "Reservations list cannot be null")
    private List<ReservationDto> reservations;

    @NotNull(message = "Alerts list cannot be null")
    private List<AlertDto> alerts;

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public List<ReservationDto> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDto> reservations) {
        this.reservations = reservations;
    }

    public List<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }

    public AirportDTO getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(AirportDTO departureAirport) {
        this.departureAirport = departureAirport;
    }

    public AirportDTO getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(AirportDTO arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }
}

