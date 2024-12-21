package com.flight.project_flight.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class); // Définir le logger

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Injecter JwtService et CustomUserDetailsService
    public SecurityConfig(JwtService jwtService, CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        logger.info("SecurityConfig initialized with JwtService and CustomUserDetailsService");
    }

    // Créer un bean JwtAuthenticationFilter
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        logger.info("Creating JwtAuthenticationFilter bean");
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, customUserDetailsService, authenticationManager);
        logger.info("JwtAuthenticationFilter bean created successfully");
        return filter;
    }

    // Configurer la chaîne de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring HttpSecurity");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter(http.getSharedObject(AuthenticationManager.class)),
                        UsernamePasswordAuthenticationFilter.class
                );

        logger.info("HttpSecurity configured successfully");
        return http.build();
    }

    // Configurer l'AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        logger.info("Configuring AuthenticationManager");

        // Configuration de l'AuthenticationManager
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        logger.info("AuthenticationManager configured successfully");

        return authenticationManager;
    }

    // Définir le UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Returning CustomUserDetailsService as UserDetailsService");
        return customUserDetailsService;
    }
}
