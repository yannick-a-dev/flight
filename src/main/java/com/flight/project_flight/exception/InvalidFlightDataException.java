package com.flight.project_flight.exception;

public class InvalidFlightDataException extends RuntimeException {

    public InvalidFlightDataException(String message) {
        super("Invalid flight data: " + message);
    }
}
