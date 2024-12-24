package com.flight.project_flight.models;

public class MessageResponse {
    private String message;

    // Constructeur
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}

