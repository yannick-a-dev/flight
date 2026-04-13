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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults()) // utilise notre CorsConfigurationSource
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints publics
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Airports - GET
                        .requestMatchers(HttpMethod.GET, "/api/airports/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/stats").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/**").permitAll()

                        //Passenger
                        .requestMatchers(HttpMethod.POST, "/api/passengers/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/passengers/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/passengers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/passengers/**").permitAll()

                        // Airports - POST, PUT, DELETE
                        .requestMatchers(HttpMethod.POST, "/api/airports/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/airports/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/airports/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/airports/**").permitAll()
                        //.requestMatchers(HttpMethod.PUT, "/api/airports/**").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.DELETE, "/api/airports/**").hasRole("ADMIN")

                        // Flights
                        //.requestMatchers(HttpMethod.POST, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/flights/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/flights/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
                        //.requestMatchers(HttpMethod.PUT, "/api/flights/**").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.GET, "/api/flights/**").authenticated()

                        // Alerts
                        .requestMatchers(HttpMethod.GET, "/api/alerts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/alerts/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/alerts/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/alerts/**").permitAll()

                        // Tout le reste
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    // ✅ Bean CORS pour Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // URL de ton front
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}