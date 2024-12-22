package com.flight.project_flight.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoderService passwordEncoderService;

    public DataInitializer(PasswordEncoderService passwordEncoderService) {
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public void run(String... args) throws Exception {
        passwordEncoderService.encodePasswordsIfNotEncoded();
    }
}
