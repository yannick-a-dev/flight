package com.flight.project_flight.repository;

import com.flight.project_flight.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    @Query("SELECT r.flight FROM Reservation r WHERE r.passenger.id = :passengerId")
    List<Flight> findFlightsByPassengerId(@Param("passengerId") Long passengerId);
}
