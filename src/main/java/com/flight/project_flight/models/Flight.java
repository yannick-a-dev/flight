package com.flight.project_flight.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flight.project_flight.config.CustomLocalDateTimeDeserializer;
import com.flight.project_flight.enums.FlightStatus;
import jakarta.persistence.*;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flight")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "flightNumber")
public class Flight {

    @Id
    private String flightNumber;

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime departureTime;

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime arrivalTime;

    private String departureAirport;
    private String arrivalAirport;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Alert> alerts = new ArrayList<>();

    // ---------- ALERTS ----------
    public void addAlert(Alert alert) {
        alerts.add(alert);
        alert.setFlight(this);
    }

    public void removeAlert(Alert alert) {
        alerts.remove(alert);
        alert.setFlight(null);
    }

    // ---------- RESERVATIONS ----------
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setFlight(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setFlight(null);
    }

    // ---------- GETTERS / SETTERS ----------
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

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }
}
