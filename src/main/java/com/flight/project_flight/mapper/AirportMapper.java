package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.service.FlightService;

public class AirportMapper {

    public static AirportDTO toDTO(Airport airport) {
        return new AirportDTO(airport);
    }

    public static Airport toEntity(AirportDTO dto) {
        Airport airport = new Airport();
        airport.setName(dto.getName() != null ? dto.getName() : "Unknown");
        airport.setCode(dto.getCode() != null ? dto.getCode().trim().toUpperCase() : "UNKNOWN");
        airport.setLocation(dto.getLocation() != null ? dto.getLocation() : "Unknown");
        airport.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 0);
        airport.setCity(dto.getCity() != null ? dto.getCity() : "Unknown");
        airport.setCountry(dto.getCountry() != null ? dto.getCountry() : "Unknown");
        airport.setInternational(dto.getInternational() != null ? dto.getInternational() : false);
        airport.setActive(dto.getIsActive() != null ? dto.getIsActive() : false);
        airport.setTerminalInfo(dto.getTerminalInfo() != null ? dto.getTerminalInfo() : "No information available");
        airport.setTimezone(dto.getTimezone() != null ? dto.getTimezone() : "Unknown");
        return airport;
    }

}



