package com.flight.project_flight.controller;

import com.flight.project_flight.dto.LoginRequest;
import com.flight.project_flight.dto.PermissionsResponse;
import com.flight.project_flight.dto.RefreshTokenRequest;
import com.flight.project_flight.dto.TokenResponse;
import com.flight.project_flight.service.AuthService;
import com.flight.project_flight.config.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthenticationController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // **Connexion utilisateur**
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authentification et génération du token d'accès
            String accessToken = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());

            // Génération du token de rafraîchissement
            String refreshToken = jwtService.generateRefreshToken(request.getUsername());

            // Réponse avec les tokens
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } catch (AuthenticationException e) {
            // Gestion des erreurs d'authentification
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse("Authentication failed: " + e.getMessage(), null));
        } catch (Exception e) {
            // Gestion des autres erreurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TokenResponse("Internal server error: " + e.getMessage(), null));
        }
    }


    // **Rafraîchir le token**
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        String newRefreshToken = jwtService.generateRefreshToken(jwtService.extractUsername(request.getRefreshToken()));
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }

    // **Vérifier les permissions**
    @GetMapping("/permissions")
    public ResponseEntity<PermissionsResponse> checkPermissions() {
        String username = authService.getCurrentUsername();
        List<String> roles = authService.getUserRoles(username);
        return ResponseEntity.ok(new PermissionsResponse(username, roles));
    }
}
