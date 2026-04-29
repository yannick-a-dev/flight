package com.flight.project_flight.service;

import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.exception.EmailAlreadyExistsException;
import com.flight.project_flight.exception.PassengerNotFoundException;
import com.flight.project_flight.mapper.AlertMapper;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.PassengerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PassengerService implements UserDetailsService {

    private final  PassengerRepository passengerRepository;
    private final AuthService authService;
    private final AlertMapper alertMapper;
    private final PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(getClass());

    public PassengerService(PassengerRepository passengerRepository, @Lazy AuthService authService, @Lazy AlertMapper alertMapper, PasswordEncoder passwordEncoder) {
        this.passengerRepository = passengerRepository;
        this.authService = authService;
        this.alertMapper = alertMapper;
        this.passwordEncoder = passwordEncoder;
    }
    public Passenger savePassenger(Passenger passenger) {
        if (passenger.getPassword() == null || passenger.getPassword().isEmpty()) {
            String defaultPassword = "default_password";
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            passenger.setPassword(encodedPassword);
        }
        passengerRepository.save(passenger);
        return passenger;
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAllWithReservationsAndFlights().stream()
                .filter(passenger -> passenger.getEmail() != null && !passenger.getEmail().isEmpty())
                .sorted(Comparator.comparing(Passenger::getId))
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

    @Transactional
    public Passenger registerPassenger(PassengerDTO passengerDTO) {
        checkEmailUniqueness(passengerDTO);
        String hashedPassword = encodePassword(passengerDTO.getPassword());
        Passenger passenger = createPassenger(passengerDTO, hashedPassword);
        if (passengerDTO.getAlerts() != null) {
            passenger.setAlerts(passengerDTO.getAlerts().stream()
                    .map(alertDto -> alertMapper.toEntity(alertDto))
                    .collect(Collectors.toList()));
        }
        return passengerRepository.save(passenger);
    }

    private void checkEmailUniqueness(PassengerDTO dto) {
        boolean exists = (dto.getId() == null)
                ? passengerRepository.existsByEmail(dto.getEmail())
                : passengerRepository.existsByEmailAndIdNot(dto.getEmail(), dto.getId());
        if (exists) {
            throw new EmailAlreadyExistsException("Passenger with this email already exists");
        }
    }

    private String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is null or empty");
        }
        String hashedPassword = passwordEncoder.encode(password);
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new RuntimeException("Password encoding failed");
        }
        return hashedPassword;
    }

    private Passenger createPassenger(PassengerDTO passengerDTO, String hashedPassword) {
        Passenger passenger = new Passenger(
                null, // id
                passengerDTO.getEmail(),
                passengerDTO.getFirstName(),
                passengerDTO.getLastName(),
                passengerDTO.getPhone(),
                passengerDTO.getPassportNumber(),
                passengerDTO.getDob(),
                new ArrayList<>(),
                new ArrayList<>(),
                hashedPassword,
                passengerDTO.getEnabled() != null ? passengerDTO.getEnabled() : true,
                new ArrayList<>()
        );

        return passenger;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Passenger userEntity = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new User(userEntity.getEmail(), userEntity.getPassword(),
                userEntity.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()));
    }

    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));
    }

    public void deletePassenger(Passenger passenger) {
        passengerRepository.delete(passenger);
    }

}
