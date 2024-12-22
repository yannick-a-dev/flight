package com.flight.project_flight.service;

import com.flight.project_flight.models.UserEntity;
import com.flight.project_flight.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordEncoderService {

    @Autowired
    private UserRepository userRepository; // Remplacez par votre repository utilisateur

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void encodePasswordsIfNotEncoded() {
        List<UserEntity> users = userRepository.findAll(); // Remplacez par votre méthode pour récupérer tous les utilisateurs
        for (UserEntity user : users) {
            String password = user.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // Mot de passe non encodé avec BCrypt
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
                userRepository.save(user);
            }
        }
    }
}
