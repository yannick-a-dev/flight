package com.flight.project_flight.dto;

import jakarta.validation.constraints.NotBlank;
public class VerifyCodeRequest {

    @NotBlank(message = "Username est obligatoire")
    private String username;

    @NotBlank(message = "Le code est obligatoire")
    private String otp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}