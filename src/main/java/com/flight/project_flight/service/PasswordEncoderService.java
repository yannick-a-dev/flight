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
    private PassengerRepository passengerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void encodePasswordsIfNotEncoded() {
        List<Passenger> users = passengerRepository.findAll(); // Récupérer tous les utilisateurs
        for (Passenger user : users) {
            String password = user.getPassword();

            if (password == null || password.isEmpty()) {
                // Attribuer un mot de passe par défaut encodé si le mot de passe est null ou vide
                System.out.println("Le mot de passe pour l'utilisateur " + user.getId() + " est null ou vide. Un mot de passe par défaut sera défini.");
                String defaultPassword = "default_password"; // Remplacez par un mot de passe sécurisé ou généré dynamiquement
                String encodedPassword = passwordEncoder.encode(defaultPassword);
                user.setPassword(encodedPassword);
                passengerRepository.save(user);
                continue; // Passer à l'utilisateur suivant
            }

            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // Encodage si le mot de passe est non-encodé
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
                passengerRepository.save(user);
            }
        }
    }
}
