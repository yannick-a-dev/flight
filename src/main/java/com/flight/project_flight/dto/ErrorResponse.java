package com.flight.project_flight.dto;

public class ErrorResponse {
    private String error;
    private String details;

    public ErrorResponse(String error, String details) {
        this.error = error;
        this.details = details;
    }
}