package com.flight.project_flight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ReservationDto {
    private Long id;
    @NotNull(message = "Reservation date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date reservationDate;
    private String seatNumber;
    private BigDecimal price;
    @NotNull(message = "Passenger ID cannot be null")
    private Long passengerId;
    @NotNull(message = "Flight ID cannot be null")
    private Long flightId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
}
