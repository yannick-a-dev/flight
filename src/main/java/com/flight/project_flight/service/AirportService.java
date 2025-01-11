package com.flight.project_flight.service;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.repository.AirportRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AirportService {
    private static final Logger logger = LoggerFactory.getLogger(AirportService.class);
    private final AirportRepository airportRepository;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<AirportDTO> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(airport -> new AirportDTO(airport))
                .collect(Collectors.toList());
    }

    public Airport getAirportById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with id: " + id));
    }

    public Airport getAirportByName(String name) {
        return airportRepository.findByName(name);
    }

    public Airport addAirport(Airport airport) {
        Set<ConstraintViolation<Airport>> violations = validator.validate(airport);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Airport> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Validation failed:\n" + sb.toString());
        }
        return airportRepository.save(airport);
    }

    public Airport updateAirport(Long id, Airport airportDetails) {
        try {
            Airport airport = getAirportById(id);
            logger.debug("Updating airport: {}", airport);
            airport.setName(airportDetails.getName());
            airport.setLocation(airportDetails.getLocation());
            airport.setCode(airportDetails.getCode());
            airport.setCapacity(airportDetails.getCapacity());
            airport.setCity(airportDetails.getCity());
            airport.setCountry(airportDetails.getCountry());
            airport.setInternational(airportDetails.getInternational());
            airport.setActive(airportDetails.getActive());
            airport.setTerminalInfo(airportDetails.getTerminalInfo());
            airport.setTimezone(airportDetails.getTimezone());

            Airport updatedAirport = airportRepository.save(airport);
            logger.debug("Updated airport: {}", updatedAirport);
            return updatedAirport;
        } catch (Exception e) {
            logger.error("Error updating airport with id {}: {}", id, e.getMessage(), e);
            throw e; // Rethrow the exception or handle it appropriately
        }
    }


    public void deleteAirport(Long id) {
        Airport airport = getAirportById(id);
        airportRepository.delete(airport);
    }
}

