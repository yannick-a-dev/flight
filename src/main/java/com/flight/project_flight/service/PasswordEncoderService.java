package com.flight.project_flight.service;

import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.PassengerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordEncoderService {

    @Autowired
    private PassengerRepository passengerRepository; // Remplacez par votre repository utilisateur

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void encodePasswordsIfNotEncoded() {
        List<Passenger> users = passengerRepository.findAll(); // Remplacez par votre méthode pour récupérer tous les utilisateurs
        for (Passenger user : users) {
            String password = user.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // Mot de passe non encodé avec BCrypt
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
                passengerRepository.save(user);
            }
        }
    }
}
