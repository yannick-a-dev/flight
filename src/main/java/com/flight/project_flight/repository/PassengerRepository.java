package com.flight.project_flight.repository;

import com.flight.project_flight.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);

    Optional<Passenger> findByEmail(String email);
}
