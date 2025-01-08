package com.flight.project_flight.repository;

import com.flight.project_flight.models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    Airport findByName(String name);

    @Query("SELECT a FROM Airport a LEFT JOIN FETCH a.departureFlights LEFT JOIN FETCH a.arrivalFlights")
    List<Airport> findAllWithFlights();


    Optional<Airport> findByCode(String code);
}
