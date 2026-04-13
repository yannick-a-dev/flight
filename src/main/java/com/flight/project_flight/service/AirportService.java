package com.flight.project_flight.service;

import com.flight.project_flight.dto.AirlineDTO;
import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.exception.AirportCodeAlreadyExistsException;
import com.flight.project_flight.external.ExternalAPIClient;
import com.flight.project_flight.mapper.AirportMapper;
import com.flight.project_flight.models.Airline;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.repository.AirlineRepository;
import com.flight.project_flight.repository.AirportRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AirportService {
    private static final Logger logger = LoggerFactory.getLogger(AirportService.class);

    private final FlightService flightService;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;

    private final ExternalAPIClient externalAPIClient;
    private final Validator validator;

    public AirportService(FlightService flightService, AirportRepository airportRepository, AirlineRepository airlineRepository, ExternalAPIClient externalAPIClient, Validator validator) {
        this.flightService = flightService;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.externalAPIClient = externalAPIClient;
        this.validator = validator;
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

    @Transactional
    public Airport addAirport(Airport airport) {
        Set<ConstraintViolation<Airport>> violations = validator.validate(airport);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Airport> v : violations) {
                sb.append(v.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Validation failed:\n" + sb);
        }
        String code = airport.getCode();
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code de l'aéroport ne peut pas être vide.");
        }
        code = code.trim().toUpperCase();
        airport.setCode(code);
        if (airportRepository.existsByCodeIgnoreCase(code)) {
            throw new AirportCodeAlreadyExistsException(code);
        }
        try {
            return airportRepository.save(airport);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException cve) {
                if (cve.getConstraintName() != null && cve.getConstraintName().contains("uk_airport_code")) {
                    throw new AirportCodeAlreadyExistsException(code);
                }
            }
            if (ex.getMessage() != null && ex.getMessage().contains("uk_airport_code")) {
                throw new AirportCodeAlreadyExistsException(code);
            }

            throw ex;
        }
    }

    public Page<AirportDTO> searchAirportsPaginated(String name, String city, String country, String code, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return airportRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (city != null && !city.isEmpty()) predicates.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            if (country != null && !country.isEmpty()) predicates.add(cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%"));
            if (code != null && !code.isEmpty()) predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(AirportMapper::toDTO);
    }


    public Airport updateAirport(Long id, Airport airportDetails) {
        try {
            Airport airport = getAirportById(id);
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
            throw e;
        }
    }


    public void deleteAirport(Long id) {
        Airport airport = getAirportById(id);
        airportRepository.delete(airport);
    }

    public List<AirportDTO> searchAirports(String name, String city, String country, String code, boolean exactNameMatch) {
        List<Airport> airports = airportRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                if (exactNameMatch) {
                    predicates.add(cb.equal(cb.lower(root.get("name")), name.toLowerCase()));
                } else {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                }
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }

            if (country != null && !country.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%"));
            }

            if (code != null && !code.isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("code")), code.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        return airports.stream().map(AirportMapper::toDTO).toList();
    }


    public Page<AirportDTO> getPaginatedAirports(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Airport> airports = airportRepository.findAll(pageable);
        try {
            return airports.map(AirportMapper::toDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public Map<String, Object> getAirportStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAirports", airportRepository.count());
        stats.put("totalFlights", flightService.countAllFlights());
        stats.put("airportsWithFlights", airportRepository.countAirportsWithFlights());
        return stats;
    }

    public void syncWithExternalAPI() {
        // ⚡ Simuler les données externes
        List<AirportDTO> externalAirports = List.of(
                new AirportDTO("JFK", "John F Kennedy", "New York", "USA", 50000, true, true, "T1", "EST"),
                new AirportDTO("LHR", "Heathrow", "London", "UK", 40000, true, true, "T2", "GMT"),
                new AirportDTO("CDG", "Charles de Gaulle", "Paris", "France", 45000, true, true, "T3", "CET")
        );

        for (AirportDTO dto : externalAirports) {
            Optional<Airport> existingOpt = airportRepository.findByCode(dto.getCode());

            if (existingOpt.isPresent()) {
                var existing = getAirport(dto, existingOpt);

                airportRepository.save(existing);
            } else {
                airportRepository.save(AirportMapper.toEntity(dto));
            }
        }
    }

    private static Airport getAirport(AirportDTO dto, Optional<Airport> existingOpt) {
        Airport existing = existingOpt.get();
        existing.setName(dto.getName());
        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        existing.setLocation(dto.getLocation());
        existing.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 0);
        existing.setInternational(dto.getInternational() != null ? dto.getInternational() : false);
        existing.setActive(dto.getIsActive() != null ? dto.getIsActive() : false);
        existing.setTerminalInfo(dto.getTerminalInfo() != null ? dto.getTerminalInfo() : "No information available");
        existing.setTimezone(dto.getTimezone() != null ? dto.getTimezone() : "Unknown");
        return existing;
    }

    public List<AirlineDTO> getAirlinesByAirport(Long airportId) {
        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found with ID: " + airportId));
        List<Airline> airlines = airlineRepository.findByAirportsContaining(airport);
        return airlines.stream().map(a -> new AirlineDTO(a.getId(), a.getName(), a.getCode())).toList();
    }
}

