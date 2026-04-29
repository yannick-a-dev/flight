package com.flight.project_flight.exception;

import com.flight.project_flight.dto.ErrorResponse;
import com.flight.project_flight.dto.TokenResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<TokenResponse> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new TokenResponse("Authentication failed: " + e.getMessage(), null, null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<TokenResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new TokenResponse("User not found: " + e.getMessage(), null, null));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AirportCodeAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAirportCodeAlreadyExists(AirportCodeAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getMostSpecificCause();

        if (rootCause != null && rootCause.getMessage() != null &&
                rootCause.getMessage().contains("Duplicate entry") &&
                rootCause.getMessage().contains("uk_airport_code")) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Un aéroport avec ce code existe déjà."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur d’intégrité des données"));
    }

    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAirportNotFound(AirportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("AIRPORT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePassengerNotFound(PassengerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }

    // ✅ UN SEUL handler global
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Une erreur inattendue est survenue"));
    }
}