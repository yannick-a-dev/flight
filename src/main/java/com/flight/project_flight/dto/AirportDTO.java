package com.flight.project_flight.dto;

import com.flight.project_flight.models.Airport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AirportDTO {

    private Long id;
    private String name;
    private String location;
    private String code;
    private Set<String> departureFlightIds;
    private Set<String> arrivalFlightIds;

    public AirportDTO() {}

    public AirportDTO(Long id, String name, String location, String code, Set<String> departureFlightIds, Set<String> arrivalFlightIds) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.code = code;
        this.departureFlightIds = departureFlightIds;
        this.arrivalFlightIds = arrivalFlightIds;
    }

    public AirportDTO(Airport airport, Set<String> departureFlightIds, Set<String> arrivalFlightIds) {
        this.id = airport.getId();
        this.code = airport.getCode();
        this.location = airport.getLocation();
        this.name = airport.getName();
        this.departureFlightIds = departureFlightIds;
        this.arrivalFlightIds = arrivalFlightIds;
    }

    public AirportDTO(Airport airport) {
        this.id = airport.getId();
        this.code = airport.getCode();
        this.location = airport.getLocation();
        this.name = airport.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<String> getDepartureFlightIds() {
        return departureFlightIds;
    }

    public void setDepartureFlightIds(Set<String> departureFlightIds) {
        this.departureFlightIds = departureFlightIds;
    }

    public Set<String> getArrivalFlightIds() {
        return arrivalFlightIds;
    }

    public void setArrivalFlightIds(Set<String> arrivalFlightIds) {
        this.arrivalFlightIds = arrivalFlightIds;
    }
}

