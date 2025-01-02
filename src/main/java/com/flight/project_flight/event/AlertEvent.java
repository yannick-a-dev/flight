package com.flight.project_flight.event;

public class AlertEvent {

   private String passportNumber;
   private String email;

    public AlertEvent(String passportNumber, String email) {
        this.passportNumber = passportNumber;
        this.email = email;
    }

    public AlertEvent() {
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
