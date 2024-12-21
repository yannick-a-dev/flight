package com.flight.project_flight.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String code;

    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departureFlights;

    @OneToMany(mappedBy = "arrivalAirport")
    private List<Flight> arrivalFlights;
}
