package com.flight.project_flight.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flight.project_flight.config.CustomLocalDateTimeDeserializer;
import com.flight.project_flight.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "alert")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String message;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime alertDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Severity severity;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "passenger_id",
            foreignKey = @ForeignKey(name = "fk_alert_passenger")
    )
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(
            name = "flight_id",
            foreignKey = @ForeignKey(name = "fk_alert_flight")
    )
    private Flight flight;

    @ManyToOne
    @JoinColumn(
            name = "ticket_id",
            foreignKey = @ForeignKey(name = "fk_alert_ticket")
    )
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

    public Alert(Passenger passenger, Flight flight, String message, Severity severityEnum, LocalDateTime alertDate) {
        this.passenger = passenger;
        this.flight = flight;
        this.message = message;
        this.severity = severityEnum;
        this.alertDate = alertDate;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                '}';
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
