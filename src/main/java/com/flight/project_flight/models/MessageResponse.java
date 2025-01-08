package com.flight.project_flight.models;

import lombok.Data;

@Data
public class MessageResponse {
    private String message;

    // Constructeur
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

