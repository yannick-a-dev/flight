package com.flight.project_flight.config;

import com.flight.project_flight.models.UserEntity;
import com.flight.project_flight.models.UserPrincipal;
import com.flight.project_flight.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        // Retrieve user entity from database
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if roles are null or empty
        if (userEntity.getRoles() == null || userEntity.getRoles().isEmpty()) {
            logger.warn("User {} has no roles assigned", username);
            try {
                throw new AccessDeniedException("User has no roles assigned");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        // Convert roles to GrantedAuthority (prefix roles with "ROLE_")
        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                .collect(Collectors.toList());

        // Return the UserPrincipal instance (or create a UserDetails directly if necessary)
        return new UserPrincipal(userEntity);
    }

    // Additional helper method to check password matching (if required)
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword); // Check if raw password matches the encoded one
    }

    // Helper method to encode raw password (if you need to encode passwords manually)
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // Encode the raw password
    }
}