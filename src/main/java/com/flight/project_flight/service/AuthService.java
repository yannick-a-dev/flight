package com.flight.project_flight.service;

import com.flight.project_flight.config.JwtService;
import com.flight.project_flight.config.JwtTokenProvider;
import com.flight.project_flight.dto.TokenResponse;
import com.flight.project_flight.models.UserEntity;
import com.flight.project_flight.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public String authenticateAndGenerateToken(String username, String password) {
        try {
            // Authentification via AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Charger l'utilisateur depuis le UserService
            User userEntity = userService.loadUserByUsername(username);

            // Génération du token JWT avec les informations de l'utilisateur
            return jwtTokenProvider.generateToken(userEntity);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<String> getUserRoles(String username) {
        // Charger l'utilisateur depuis la base de données pour récupérer ses rôles
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Récupérer les rôles de l'utilisateur
        return userEntity.getRoles().stream()
                .map(role -> role.getName())  // suppose que "getName" retourne le nom du rôle
                .collect(Collectors.toList());
    }
}

