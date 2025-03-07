package com.flight.project_flight.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flight.project_flight.config.CustomLocalDateTimeDeserializer;
import com.flight.project_flight.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "alert")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String message;

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime alertDate;

    @NotNull
    private Severity severity;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    @JsonBackReference("alert-passenger")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    @JsonBackReference("alert-flight")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonBackReference("ticket-alerts")
    private Ticket ticket;

    public Alert() {}

    public Alert(String message, LocalDateTime alertDate, Severity severity, Passenger passenger, Flight flight, Ticket ticket) {
        this.message = message;
        this.alertDate = alertDate;
        this.severity = severity;
        this.passenger = passenger;
        this.flight = flight;
        this.ticket = ticket;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(LocalDateTime alertDate) {
        this.alertDate = alertDate;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
