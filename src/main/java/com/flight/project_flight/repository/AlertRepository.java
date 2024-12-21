package com.flight.project_flight.repository;

import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
}
