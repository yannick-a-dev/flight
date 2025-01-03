package com.flight.project_flight.controller;

import com.flight.project_flight.dto.*;
import com.flight.project_flight.models.MessageResponse;
import com.flight.project_flight.models.PassengerRequest;
import com.flight.project_flight.service.AuthService;
import com.flight.project_flight.config.JwtService;
import com.flight.project_flight.service.PassengerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    @Lazy
    private AuthService authService;
    private final JwtService jwtService;
    @Autowired
    private PassengerService passengerService;

    public AuthenticationController(JwtService jwtService, PassengerService passengerService) {
        this.jwtService = jwtService;
        this.passengerService = passengerService;
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.debug("Login request received: {}", request);

        try {
            // Authentification et génération des tokens
            logger.debug("Authenticating user: {}", request.getUsername());
            String accessToken = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());

            logger.debug("Generating refresh token for user: {}", request.getUsername());
            String refreshToken = jwtService.generateRefreshToken(request.getUsername());

            logger.debug("Fetching access token expiry for user: {}", request.getUsername());
            String expiresIn = String.valueOf(jwtService.getAccessTokenExpiry());

            logger.info("Authentication successful for user: {}", request.getUsername());
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, expiresIn));

        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse("Authentication failed", null, null));
        } catch (Exception e) {
            logger.error("Unexpected error during login for user {}: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TokenResponse("Internal server error", null, null));
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerPassenger(@Valid @RequestBody PassengerRequest passengerRequest) {
        try {
            passengerService.registerPassenger(passengerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Passenger registered successfully"));
        } catch (Exception e) {
            if (e.getMessage().contains("Passenger with similar data already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)  // Définir explicitement le type de contenu
                        .body(new ErrorResponse("Conflict", "Passenger with similar data already exists"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)  // Définir explicitement le type de contenu
                        .body(new ErrorResponse("Internal server error", "Please try again later"));
            }
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
