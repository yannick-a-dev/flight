package com.flight.project_flight.service;

import com.flight.project_flight.dto.PassengerDTO;
import com.flight.project_flight.exception.EmailAlreadyExistsException;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.PassengerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PassengerService implements UserDetailsService {
    @Autowired
    private  PassengerRepository passengerRepository;
    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(getClass());

    public PassengerService(@Lazy AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
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
        return passengerRepository.findAll().stream()
                .filter(passenger -> passenger.getEmail() != null && !passenger.getEmail().isEmpty())  // Filtrage des passagers avec un email non vide
                .sorted(Comparator.comparing(Passenger::getId))  // Trie uniquement par ID de manière croissante
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
        logger.debug("passengerDTO received: {}", passengerDTO);
        checkEmailUniqueness(passengerDTO);
        logger.debug("Raw password from request: {}", passengerDTO.getPassword());
        String hashedPassword = encodePassword(passengerDTO.getPassword());
        logger.debug("Hashed password: {}", hashedPassword);
        Passenger passenger = createPassenger(passengerDTO, hashedPassword);
        logger.info("Passenger to be saved: {}", passenger);
        Passenger savedPassenger = passengerRepository.save(passenger);
        logger.info("Passenger saved successfully: {}", savedPassenger);
        return savedPassenger;
    }


    private void checkEmailUniqueness(PassengerDTO passengerDTO) {
        if (passengerDTO.getId() != null && passengerRepository.existsByEmailAndIdNot(passengerDTO.getEmail(), passengerDTO.getId())) {
            throw new EmailAlreadyExistsException("Passenger with this email already exists");
        } else if (passengerDTO.getId() == null && passengerRepository.existsByEmail(passengerDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Passenger with this email already exists");
        }
    }
    private String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is null or empty");
        }
        logger.debug("Encoding password for user: {}", password);
        String hashedPassword = passwordEncoder.encode(password);
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new RuntimeException("Password encoding failed");
        }
        return hashedPassword;
    }


    private Passenger createPassenger(PassengerDTO passengerDTO, String hashedPassword) {
        Passenger passenger = new Passenger(
                null,
                passengerDTO.getFirstName(),
                passengerDTO.getLastName(),
                passengerDTO.getEmail(),
                hashedPassword,
                passengerDTO.getPhone(),
                passengerDTO.getPassportNumber(),
                passengerDTO.getDob()
        );
        if (passengerDTO.getEnabled() == null) {
            passenger.setEnabled(true);
        } else {
            passenger.setEnabled(passengerDTO.getEnabled());
        }
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
        return passengerRepository.findById(id).orElseThrow(() -> new RuntimeException("Passenger not found"));
    }
}
