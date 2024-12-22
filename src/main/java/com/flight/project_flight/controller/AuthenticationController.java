package com.flight.project_flight.controller;

import com.flight.project_flight.dto.*;
import com.flight.project_flight.service.AuthService;
import com.flight.project_flight.config.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthenticationController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String accessToken = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
            String refreshToken = jwtService.generateRefreshToken(request.getUsername());
            String expiresIn = String.valueOf(jwtService.getAccessTokenExpiry()); // Méthode pour récupérer la durée de validité
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, expiresIn));
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse("Authentication failed", null, null)); // Generic message in production
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TokenResponse("Internal server error", null, null)); // Generic message in production
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        String newRefreshToken = jwtService.generateRefreshToken(jwtService.extractUsername(request.getRefreshToken()));
        String expiresIn = String.valueOf(jwtService.getAccessTokenExpiry());
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken, expiresIn));
    }


    @GetMapping("/permissions")
    public ResponseEntity<?> checkPermissions() {
        try {
            String username = authService.getCurrentUsername();
            if (username == null) {
                throw new BadCredentialsException("User not authenticated");
            }
            List<String> roles = authService.getUserRoles(username);
            return ResponseEntity.ok(new PermissionsResponse(username, roles));
        } catch (BadCredentialsException e) {
            logger.warn("Permission check failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while checking permissions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Please try again later"));
        }
    }

}
