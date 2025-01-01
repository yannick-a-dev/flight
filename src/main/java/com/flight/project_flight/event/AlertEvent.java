package com.flight.project_flight.event;

public class AlertEvent {
   private String email;

    public AlertEvent(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
