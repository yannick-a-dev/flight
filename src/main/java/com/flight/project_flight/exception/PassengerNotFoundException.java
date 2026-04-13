package com.flight.project_flight.exception;

public class PassengerNotFoundException extends RuntimeException{
    public PassengerNotFoundException(Long id) {
        super("Passenger not found with id: " + id);
    }
}
