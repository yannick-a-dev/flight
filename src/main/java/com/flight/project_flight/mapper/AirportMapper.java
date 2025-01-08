package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AirportDTO;
import com.flight.project_flight.models.Airport;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.service.FlightService;

import java.util.*;
import java.util.stream.Collectors;

public class AirportMapper {

    public static AirportDTO toDTO(Airport airport) {
        // Récupérer les numéros de vols de départ (String)
        Set<String> departureFlightIds = airport.getDepartureFlights() != null ?
                airport.getDepartureFlights().stream()
                        .map(flight -> flight.getFlightNumber())  // Utilisation du numéro de vol
                        .collect(Collectors.toSet()) : new HashSet<>();

        // Récupérer les numéros de vols d'arrivée (String)
        Set<String> arrivalFlightIds = airport.getArrivalFlights() != null ?
                airport.getArrivalFlights().stream()
                        .map(flight -> flight.getFlightNumber())  // Utilisation du numéro de vol
                        .collect(Collectors.toSet()) : new HashSet<>();

        return new AirportDTO(
                airport.getId(),
                airport.getName(),
                airport.getLocation(),
                airport.getCode(),
                departureFlightIds,
                arrivalFlightIds
        );
    }

    public static Airport toEntity(AirportDTO airportDTO, FlightService flightService) {
        Airport airport = new Airport();
        airport.setId(airportDTO.getId());
        airport.setName(airportDTO.getName());
        airport.setLocation(airportDTO.getLocation());
        airport.setCode(airportDTO.getCode());

        // Mapper les départs (Convertir Set<Long> en List<String>)
        List<String> departureFlightNumbers = airportDTO.getDepartureFlightIds().stream()
                .map(String::valueOf) // Convertir Long en String
                .collect(Collectors.toList());
        Set<Flight> departureFlights = mapFlights(departureFlightNumbers, flightService);
        airport.setDepartureFlights(departureFlights);

        // Mapper les arrivées (Convertir Set<Long> en List<String>)
        List<String> arrivalFlightNumbers = airportDTO.getArrivalFlightIds().stream()
                .map(String::valueOf) // Convertir Long en String
                .collect(Collectors.toList());
        Set<Flight> arrivalFlights = mapFlights(arrivalFlightNumbers, flightService);
        airport.setArrivalFlights(arrivalFlights);

        return airport;
    }

    private static Set<Flight> mapFlights(List<String> flightNumbers, FlightService flightService) {
        if (flightNumbers == null || flightNumbers.isEmpty()) {
            return Collections.emptySet(); // Retourne un Set vide si pas de numéros de vol
        }

        return flightNumbers.stream()
                .map(flightNumber -> flightService.findByFlightNumber(flightNumber) // Assurez-vous que findByFlightNumber retourne un Optional<Flight>
                        .orElse(null)) // Retourne null si le vol n'est pas trouvé
                .filter(Objects::nonNull)  // Filtre les vols non trouvés
                .collect(Collectors.toSet());
    }

}


