package com.flight.project_flight.repository;

import com.flight.project_flight.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FlightRepository extends JpaRepository<Flight, String> {
    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
    Optional<Flight> findByFlightNumber(@Param("flightNumber") String flightNumber);

    @Query("SELECT r.flight FROM Reservation r WHERE r.passenger.id = :passengerId")
    List<Flight> findFlightsByPassengerId(@Param("passengerId") Long passengerId);
}
