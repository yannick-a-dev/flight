package com.flight.project_flight.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
