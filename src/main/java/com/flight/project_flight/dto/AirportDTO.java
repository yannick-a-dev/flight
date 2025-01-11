package com.flight.project_flight.dto;

import com.flight.project_flight.models.Airport;

public class AirportDTO {
    private Long id;
    private String name;
    private String location;
    private String code;
    private Integer capacity;
    private String city;
    private String country;
    private Boolean international;
    private Boolean isActive;
    private String terminalInfo;
    private String timezone;

    public AirportDTO() {
    }

    public AirportDTO(Airport airport) {
        this.id = airport.getId();
        this.name = airport.getName();
        this.location = airport.getLocation();
        this.code = airport.getCode();
        this.capacity = airport.getCapacity();
        this.city = airport.getCity();
        this.country = airport.getCountry();
        this.international = airport.getInternational();
        this.isActive = airport.getActive();
        this.terminalInfo = airport.getTerminalInfo();
        this.timezone = airport.getTimezone();
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}


