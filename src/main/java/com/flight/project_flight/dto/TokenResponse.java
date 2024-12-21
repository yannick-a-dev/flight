package com.flight.project_flight.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String token;

    public TokenResponse(String accessToken, String refreshToken) {
    }
}
