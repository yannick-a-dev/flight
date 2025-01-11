package com.flight.project_flight.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "airport")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Location is mandatory")
    @Size(min = 1, message = "Location must not be empty")
    private String location;

    @NotNull(message = "Code is mandatory")
    @Size(min = 1, message = "Code must not be empty")
    private String code;

    @NotNull
    @Min(value = 0, message = "Capacity must be positive")
    @Column(nullable = false)
    private Integer capacity = 0;

    @NotNull
    @Column(nullable = false)
    private String city = "Unknown";

    @NotNull
    @Column(nullable = false)
    private String country = "Unknown";

    @NotNull
    @Column(nullable = false)
    private Boolean international = false;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @NotNull
    @Column(nullable = false)
    private String terminalInfo = "No information available";

    @NotNull
    @Column(nullable = false)
    private String timezone = "Unknown";

    @JsonIgnore
    @OneToMany(mappedBy = "departureAirport", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Flight> departureFlights = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "arrivalAirport", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Flight> arrivalFlights = new HashSet<>();

    // Constructeur par défaut
    public Airport() {
    }

    // Constructeur avec paramètres
    public Airport(Long id, String name, String location, String code, Integer capacity, String city, String country, Boolean international, Boolean isActive, String terminalInfo, String timezone) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.code = code;
        this.capacity = capacity != null ? capacity : 0;
        this.city = city != null ? city : "Unknown";
        this.country = country != null ? country : "Unknown";
        this.international = international != null ? international : false;
        this.isActive = isActive != null ? isActive : false;
        this.terminalInfo = terminalInfo != null ? terminalInfo : "No information available";
        this.timezone = timezone != null ? timezone : "Unknown";
    }

    // Getters et Setters
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getInternational() {
        return international;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
