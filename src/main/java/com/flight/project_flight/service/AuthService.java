package com.flight.project_flight.service;

import com.flight.project_flight.config.JwtTokenProvider;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private  PassengerService passengerService;
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       PassengerService passengerService, PassengerRepository passengerRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;

    }

    public String authenticateAndGenerateToken(String username, String password) {
        try {
            // Authentification via AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Charger l'utilisateur depuis le UserService
            UserDetails userDetails = passengerService.loadUserByUsername(username);

            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Génération du token JWT avec les informations de l'utilisateur
            return jwtTokenProvider.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid username or password") {};
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("User not found: " + e.getMessage()) {};
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage()) {};
        }
    }
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<String> getUserRoles(String email) {
        // Charger l'utilisateur depuis la base de données pour récupérer ses rôles
        Passenger userEntity = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Récupérer les rôles de l'utilisateur
        return userEntity.getRoles().stream()
                .map(role -> role.getName())  // suppose que "getName" retourne le nom du rôle
                .collect(Collectors.toList());
    }


    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}

