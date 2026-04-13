package com.flight.project_flight.controller;

import com.flight.project_flight.config.JwtService;
import com.flight.project_flight.dto.*;
import com.flight.project_flight.exception.EmailAlreadyExistsException;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.service.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolation;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(getClass());
    private final AuthService authService;
    private final JwtService jwtService;
    private final PassengerService passengerService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final ForgotPasswordService forgotPasswordService;

    public AuthenticationController(@Lazy AuthService authService, JwtService jwtService, PassengerService passengerService, EmailService emailService, OtpService otpService, ForgotPasswordService forgotPasswordService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.passengerService = passengerService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.forgotPasswordService = forgotPasswordService;
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            // 1️⃣ Vérification des identifiants
            try {
                authService.verifyCredentials(request.getUsername(), request.getPassword());
            } catch (BadCredentialsException e) {
                response.put("message", "Identifiants invalides");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } catch (Exception e) {
                e.printStackTrace(); // debug complet
                response.put("message", "Erreur lors de la vérification des identifiants: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // 2️⃣ Génération du code OTP
            String otpCode;
            try {
                otpCode = otpService.generateOtp(request.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("message", "Erreur lors de la génération du code OTP: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // 3️⃣ Envoi de l'email
            try {
                emailService.sendOtpCode(request.getUsername(), otpCode);
            } catch (Exception e) {
                e.printStackTrace();
                response.put("message", "Erreur lors de l'envoi de l'email OTP: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // 4️⃣ Réponse OK
            response.put("message", "Un code de vérification a été envoyé par mail.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Catch global, si jamais une autre exception imprévue se produit
            e.printStackTrace();
            response.put("message", "Erreur interne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/verifyCode", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            logger.debug("Vérification OTP pour l'utilisateur: {}", request.getUsername());

            // Vérifier si le code est correct
            if (!otpService.validateOtp(request.getUsername(), request.getOtp())) {
                logger.warn("OTP invalide pour l'utilisateur {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenResponse("Code invalide", null, null));
            }

            // Générer les tokens après validation du code
            String accessToken = jwtService.generateAccessToken(request.getUsername());
            String refreshToken = jwtService.generateRefreshToken(request.getUsername());
            String expiresIn = String.valueOf(jwtService.getAccessTokenExpiry());

            logger.info("OTP validé et tokens générés pour {}", request.getUsername());
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, expiresIn));

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification OTP pour {} : {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TokenResponse("Erreur interne: " + e.getMessage(), null, null));
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerPassenger(@Valid @RequestBody PassengerDTO passengerDTO) {
        try {
            Passenger savedPassenger = passengerService.registerPassenger(passengerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPassenger);

        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Conflict", e.getMessage()));

        } catch (ConstraintViolationException e) {
            String details = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Bad Request", details));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        String newRefreshToken = jwtService.generateRefreshToken(jwtService.extractUsername(request.getRefreshToken()));
        String expiresIn = String.valueOf(jwtService.getAccessTokenExpiry());
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken, expiresIn));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            // Génération et envoi du token
            forgotPasswordService.generateAndSendToken(email);
            return ResponseEntity.ok(Map.of("message", "Un lien de réinitialisation a été envoyé à votre adresse email."));
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du lien de réinitialisation pour {} : {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne : " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            forgotPasswordService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        } catch (RuntimeException e) {
            logger.warn("Échec réinitialisation mot de passe : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la réinitialisation du mot de passe : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne"));
        }
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
