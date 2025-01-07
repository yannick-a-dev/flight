package com.flight.project_flight.enums;

public enum FlightStatus {
    ON_TIME("ON_TIME"),
    CANCELLED("CANCELLED"),
    DELAYED("DELAYED"),
    BOARDING("BOARDING"),
    LANDED("LANDED");

    private final String status;

    // Constructeur pour initialiser la valeur de chaque statut
    FlightStatus(String status) {
        this.status = status;
    }

    // Méthode pour obtenir la valeur du statut sous forme de chaîne de caractères
    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    // Méthode pour récupérer un FlightStatus à partir de la chaîne
    public static FlightStatus fromString(String status) {
        for (FlightStatus flightStatus : FlightStatus.values()) {
            if (flightStatus.status.equalsIgnoreCase(status)) {
                return flightStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}

