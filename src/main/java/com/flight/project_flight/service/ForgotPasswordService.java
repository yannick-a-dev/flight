package com.flight.project_flight.service;

import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.PasswordResetToken;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final PasswordResetTokenRepository tokenRepository;
    private final PassengerRepository passengerRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * Génère un token et envoie un email pour la réinitialisation
     * Pas d'exception 500 si l'email n'existe pas ou mail non envoyé
     */
    public void generateAndSendToken(String email) {
        Optional<Passenger> userOpt = passengerRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Email inconnu → ne rien faire, sécurité
            System.out.println("Tentative de reset pour email inconnu : " + email);
            return;
        }

        Passenger user = userOpt.get();

        // Génération d’un token unique
        String token = UUID.randomUUID().toString();

        // Création et sauvegarde du token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(resetToken);

        // Lien de réinitialisation
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        // Préparation du mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de mot de passe");
        message.setText("Bonjour,\n\nPour réinitialiser votre mot de passe, cliquez sur ce lien : "
                + resetUrl + "\n\nSi vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\nMerci.");

        try {
            mailSender.send(message);
            System.out.println("Email de réinitialisation envoyé à " + email);
        } catch (Exception e) {
            e.printStackTrace();
            // Ne pas planter le backend → loguer seulement
            System.out.println("Impossible d'envoyer l'email de réinitialisation pour " + email);
        }
    }

    /**
     * Vérifie si un token est valide
     */
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired())
                .isPresent();
    }

    /**
     * Réinitialise le mot de passe pour un token donné
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return false;
        }

        Optional<Passenger> userOpt = passengerRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) return false;

        Passenger user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        passengerRepository.save(user);

        // Supprimer le token après usage
        tokenRepository.delete(resetToken);

        return true;
    }
}
