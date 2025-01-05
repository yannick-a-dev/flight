package com.flight.project_flight.exception;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String flightNumber) {
        super("Flight not found with flightNumber: " + flightNumber);
    }
}