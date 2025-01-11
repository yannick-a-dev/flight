package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.service.FlightService;

public class AirportMapper {

    public static AirportDTO toDTO(Airport airport) {
        return new AirportDTO(airport);
    }

    public static Airport toEntity(AirportDTO airportDTO, FlightService flightService) {
        Airport airport = new Airport();
        airport.setId(airportDTO.getId());
        airport.setName(airportDTO.getName());
        airport.setLocation(airportDTO.getLocation());
        airport.setCode(airportDTO.getCode());
        airport.setCapacity(airportDTO.getCapacity());
        airport.setCity(airportDTO.getCity());
        airport.setCountry(airportDTO.getCountry());
        airport.setInternational(airportDTO.getInternational());
        airport.setActive(airportDTO.getIsActive());
        airport.setTerminalInfo(airportDTO.getTerminalInfo());
        airport.setTimezone(airportDTO.getTimezone());
        return airport;
    }
}


