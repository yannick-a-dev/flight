package com.flight.project_flight.dto;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    // Constructeur avec initialisation
    public TokenResponse(String accessToken, String refreshToken, String expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getter et Setter pour accessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter et Setter pour refreshToken
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

