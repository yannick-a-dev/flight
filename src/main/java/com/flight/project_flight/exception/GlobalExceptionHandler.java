package com.flight.project_flight.exception;

import com.flight.project_flight.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TokenResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new TokenResponse("Internal server error: " + e.getMessage(), null, null));
    }
}

