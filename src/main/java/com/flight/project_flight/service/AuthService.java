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
    private final UserRepository userRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * Authentifie un utilisateur et génère un token JWT.
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return Le token JWT généré
     */
    public String authenticateAndGenerateToken(String username, String password) {
        try {
            // Authentification via AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Charger l'utilisateur depuis la base de données
            UserEntity userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            // Génération du token JWT avec les informations de l'utilisateur
            return jwtTokenProvider.generateToken((UserDetails) userEntity);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Récupère le nom d'utilisateur de l'utilisateur authentifié.
     * @return Le nom d'utilisateur
     */
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Récupère les rôles de l'utilisateur donné.
     * @param username Nom d'utilisateur
     * @return Liste des rôles de l'utilisateur
     */
    public List<String> getUserRoles(String username) {
        // Charger l'utilisateur depuis la base de données pour récupérer ses rôles
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return userEntity.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
    }
}

