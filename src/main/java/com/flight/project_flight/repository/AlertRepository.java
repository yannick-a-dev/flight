package com.flight.project_flight.repository;

import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByPassengerId(Long passengerId);

    Alert findByPassengerAndFlightAndMessageAndAlertDate(Passenger passenger, Flight flight, String message, Date alertDate);
}
