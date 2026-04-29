package com.flight.project_flight.dto;

import java.time.LocalDateTime;

public class AlertResponseDto {
    private Long id;

    private String message;
    private LocalDateTime alertDate;
    private String severity;

    private Long passengerId;

    private String flightNumber;

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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
