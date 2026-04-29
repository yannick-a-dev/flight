package com.flight.project_flight.exception;

public class AirportNotFoundException extends RuntimeException {
    public AirportNotFoundException(String airportCode) {
        super("Airport with code " + airportCode + " not found.");
    }
}
