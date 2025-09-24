package com.flight.project_flight.repository;

import com.flight.project_flight.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    // Trouver un token par sa valeur
    Optional<PasswordResetToken> findByToken(String token);

    // Optionnel : trouver un token par email (utile pour invalidation si nécessaire)
    Optional<PasswordResetToken> findByEmail(String email);
}
