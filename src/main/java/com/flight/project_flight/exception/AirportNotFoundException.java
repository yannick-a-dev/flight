package com.flight.project_flight.exception;

public class AirportNotFoundException extends RuntimeException {

    // Constructeur avec un message d'erreur
    public AirportNotFoundException(String airportCode) {
        super("Airport with code " + airportCode + " not found.");
    }

    // Vous pouvez ajouter d'autres constructeurs si nécessaire
    public AirportNotFoundException(String airportCode, Throwable cause) {
        super("Airport with code " + airportCode + " not found.", cause);
    }
}
