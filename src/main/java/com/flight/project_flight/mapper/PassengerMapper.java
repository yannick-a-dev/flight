package com.flight.project_flight.mapper;

import com.flight.project_flight.dto.AlertDto;
import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.models.Passenger;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PassengerMapper {
    public PassengerDTO convertToDto(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setPhone(passenger.getPhone());
        dto.setPassportNumber(passenger.getPassportNumber());
        dto.setDob(passenger.getDob());
        dto.setEnabled(passenger.getEnabled());

        // Conversion des alertes en évitant NullPointer
        List<AlertDto> alertDtos = Optional.ofNullable(passenger.getAlerts())
                .orElse(Collections.emptyList())
                .stream()
                .map(alert -> {
                    AlertDto alertDto = new AlertDto();
                    alertDto.setId(alert.getId());
                    alertDto.setMessage(alert.getMessage());
                    return alertDto;
                })
                .collect(Collectors.toList());

        // Conversion des numéros de vols
        List<String> flightNumbers = Optional.ofNullable(passenger.getReservations())
                .orElse(Collections.emptyList())
                .stream()
                .map(reservation -> reservation.getFlight().getFlightNumber())
                .collect(Collectors.toList());

        dto.setFlightNumbers(flightNumbers);
        dto.setAlerts(alertDtos);
        return dto;
    }

    public void updatePassengerDetails(Passenger passenger, PassengerDTO passengerDTO) {
        passenger.setFirstName(passengerDTO.getFirstName());
        passenger.setLastName(passengerDTO.getLastName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPhone(passengerDTO.getPhone());
        passenger.setPassportNumber(passengerDTO.getPassportNumber());
        passenger.setDob(passengerDTO.getDob());
    }

}
