package com.flight.project_flight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize

                        // Endpoints publics
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Airports - GET
                        .requestMatchers(HttpMethod.GET, "/api/airports/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/stats").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/airports/**").permitAll()

                        // Airports - POST, PUT, DELETE
                        .requestMatchers(HttpMethod.POST, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/airports/**").hasRole("ADMIN")

                        // Flights
                        .requestMatchers(HttpMethod.POST, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/flights/**").authenticated()

                        // Tout le reste
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }
}
