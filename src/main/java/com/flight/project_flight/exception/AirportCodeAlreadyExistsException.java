package com.flight.project_flight.exception;

public class AirportCodeAlreadyExistsException extends RuntimeException {
    public AirportCodeAlreadyExistsException(String code) {
        super("Un aéroport avec le code '" + code + "' existe déjà.");
    }
}
