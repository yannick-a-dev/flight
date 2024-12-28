package com.flight.project_flight.service;

import com.flight.project_flight.exception.EmailAlreadyExistsException;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.PassengerRequest;
import com.flight.project_flight.models.PasswordHasher;
import com.flight.project_flight.repository.PassengerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Transactional
    public Passenger registerPassenger(PassengerRequest passengerRequest) {
        // Vérification si l'email existe déjà pour un passager différent de celui en cours (exclure l'ID actuel)
        if (passengerRequest.getId() != null && passengerRepository.existsByEmailAndIdNot(passengerRequest.getEmail(), passengerRequest.getId())) {
            throw new EmailAlreadyExistsException("Passenger with this email already exists");
        } else if (passengerRequest.getId() == null && passengerRepository.existsByEmail(passengerRequest.getEmail())) {
            // Si c'est une création, vérifier si l'email existe déjà
            throw new EmailAlreadyExistsException("Passenger with this email already exists");
        }

        // Hachage du mot de passe
        String hashedPassword = passwordEncoder.encode(passengerRequest.getPassword());

        Passenger passenger = new Passenger(
                null,
                passengerRequest.getFirstName(),
                passengerRequest.getLastName(),
                passengerRequest.getEmail(),
                hashedPassword,
                passengerRequest.getPhone(),
                passengerRequest.getPassportNumber(),
                passengerRequest.getDob(),
                null,
                null
        );

        logger.info("Passenger to be saved: {}", passenger);

        // Si un passager existe avec cet ID, on met à jour les informations
        if (passengerRequest.getId() != null) {
            passenger.setId(passengerRequest.getId());
        }

        logger.info("Saving passenger: {}", passenger);
        Passenger savedPassenger = passengerRepository.save(passenger);
        logger.info("Passenger saved successfully: {}", savedPassenger);
        return savedPassenger;
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
