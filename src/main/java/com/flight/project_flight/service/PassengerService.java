package com.flight.project_flight.service;

import com.flight.project_flight.exception.PassengerNotFoundException;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Passenger savePassenger(Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .filter(passenger -> passenger.getEmail() != null && !passenger.getEmail().isEmpty())
                .sorted(Comparator.comparing(Passenger::getLastName))
                .collect(Collectors.toList());
    }

    public List<String> getPassengerNames() {
        return passengerRepository.findAll().stream()
                .filter(passenger -> passenger.getFirstName() != null && !passenger.getFirstName().isEmpty())
                .map(passenger -> passenger.getFirstName().toUpperCase() + " " + passenger.getLastName().toUpperCase())
                .collect(Collectors.toList());
    }

    public Optional<Passenger> getPassengerById(Long id) {
        return passengerRepository.findById(id);
    }

    public boolean deletePassengerById(Long id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
