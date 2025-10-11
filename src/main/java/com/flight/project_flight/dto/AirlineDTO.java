package com.flight.project_flight.dto;

public class AirlineDTO {
    private Long id;
    private String name;
    private String iataCode;

    public AirlineDTO() {}

    public AirlineDTO(Long id, String name, String iataCode) {
        this.id = id;
        this.name = name;
        this.iataCode = iataCode;
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

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }
}
