package com.flight.project_flight.repository;

import com.flight.project_flight.models.Airline;
import com.flight.project_flight.models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    List<Airline> findByAirportsContaining(Airport airport);
}
