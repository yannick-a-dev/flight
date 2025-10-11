package com.flight.project_flight.repository;

import com.flight.project_flight.models.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long>, JpaSpecificationExecutor<Airport> {
    Airport findByName(String name);

    @Query("SELECT a FROM Airport a LEFT JOIN FETCH a.departureFlights LEFT JOIN FETCH a.arrivalFlights")
    List<Airport> findAllWithFlights();

    Optional<Airport> findByCode(String code);

    @Query("SELECT COUNT(a) FROM Airport a WHERE SIZE(a.flights) > 0")
    long countAirportsWithFlights();

    Airport findByIataCode(String iataCode);
}
