package com.flight.project_flight.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "airport")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String code;

    @OneToMany(mappedBy = "departureAirport", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Flight> departureFlights = new HashSet<>();

    @OneToMany(mappedBy = "arrivalAirport", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Flight> arrivalFlights = new HashSet<>();

    public Airport() {

    }

    public Airport(Long id, String name, String location, String code, Set<Flight> departureFlights, Set<Flight> arrivalFlights) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.code = code;
        this.departureFlights = departureFlights;
        this.arrivalFlights = arrivalFlights;
    }

    public Airport(Long id, String code, String location, String name) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.code = code;
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

    public Set<Flight> getDepartureFlights() {
        return departureFlights;
    }

    public void setDepartureFlights(Set<Flight> departureFlights) {
        this.departureFlights = departureFlights;
    }

    public Set<Flight> getArrivalFlights() {
        return arrivalFlights;
    }

    public void setArrivalFlights(Set<Flight> arrivalFlights) {
        this.arrivalFlights = arrivalFlights;
    }
}
