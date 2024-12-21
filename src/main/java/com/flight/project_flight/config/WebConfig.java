package com.flight.project_flight.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class); // Définir le logger

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        logger.info("Configuring CORS mappings...");
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                logger.info("Adding CORS mapping for URL pattern /**");
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // Origine du frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization");
                logger.info("CORS mapping added for allowed origins: http://localhost:4200");
            }
        };
    }
}

