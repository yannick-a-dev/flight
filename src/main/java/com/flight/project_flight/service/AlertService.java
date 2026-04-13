package com.flight.project_flight.service;

import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.event.AlertEvent;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
import com.flight.project_flight.repository.FlightRepository;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@EnableKafka
public class AlertService {

    private final FlightRepository flightRepository;
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private static final Schema SCHEMA;

    static {
        try (InputStream inputStream = AlertService.class.getClassLoader().getResourceAsStream("avro/event-placed.avsc")) {
            if (inputStream == null) {
                throw new RuntimeException("Schema file not found!");
            }
            SCHEMA = new Schema.Parser().parse(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing Avro schema", e);
        }
    }


//    private static final String PASSWORD_NUMBER_FIELD = "passwordNumber";
//    private static final String EMAIL_FIELD = "email";
//    private static final String FIRST_NAME_FIELD = "firstName";
//    private static final String LAST_NAME_FIELD = "lastName";

    private final AlertRepository alertRepository;
    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public AlertService(FlightRepository flightRepository, AlertRepository alertRepository, KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.flightRepository = flightRepository;
        this.alertRepository = alertRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Alert createAlertForPassenger(
            Passenger passenger,
            Flight flight,
            LocalDateTime alertDate,
            String message,
            String severity) {

        MDC.put("passengerId", passenger.getId().toString());

        try {
            validateInputs(passenger, flight, alertDate, message, severity);
            Severity severityEnum = Severity.valueOf(severity.toUpperCase());
            Alert alert = new Alert(passenger, flight, message, severityEnum, alertDate);
            Alert savedAlert = alertRepository.save(alert);
            AlertEvent event = createAlertEvent(savedAlert);
            sendAlertEventToKafka(event);
            return savedAlert;
        } finally {
            MDC.clear();
        }
    }

    @Retryable(
            retryFor = {SerializationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    private void sendAlertEventToKafka(AlertEvent alertEvent) {
        log.debug("Preparing to send AlertEvent to Kafka: {}", alertEvent);

        try {
            GenericRecord record = createAvroRecord(alertEvent);
            kafkaTemplate.send("event-placed", record).exceptionally(ex -> {
                log.error("Error sending AlertEvent to Kafka", ex);
                return null;
            });
        } catch (SerializationException e) {
            log.error("Serialization error", e);
        }
    }

    public GenericRecord createAvroRecord(AlertEvent event) {

        GenericRecord record = new GenericData.Record(SCHEMA);

        record.put("passwordNumber", event.getPasswordNumber());
        record.put("email", event.getEmail());
        record.put("firstName", event.getFirstName());
        record.put("lastName", event.getLastName());

        record.put("alertId", event.getAlertId());
        record.put("message", event.getMessage());
        record.put("severity", event.getSeverity());
        record.put("alertDate", event.getAlertDate().toString());
        record.put("flightNumber", event.getFlightNumber());

        return record;
    }

    private void validateInputs(Passenger passenger, Flight flight, LocalDateTime alertDate, String message, String severity) {
        if (passenger == null || flight == null || alertDate == null || message == null || severity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All input parameters must be provided");
        }
        if (!isValidEmail(passenger.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
        if (message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must not be empty");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    private Severity parseSeverity(String severity) {
        try {
            return Severity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Invalid severity value: '%s'. Allowed values are: %s", severity, Arrays.toString(Severity.values())), e);
        }
    }

    private AlertEvent createAlertEvent(Alert alert) {

        Passenger passenger = alert.getPassenger();
        Flight flight = alert.getFlight();

        AlertEvent alertEvent = new AlertEvent();

        // Passenger
        alertEvent.setPasswordNumber(passenger.getPassportNumber());
        alertEvent.setEmail(passenger.getEmail());
        alertEvent.setFirstName(passenger.getFirstName());
        alertEvent.setLastName(passenger.getLastName());

        // Alert (IMPORTANT)
        alertEvent.setMessage(alert.getMessage());
        alertEvent.setSeverity(alert.getSeverity().name());
        alertEvent.setAlertDate(alert.getAlertDate().toString());

        // Flight (IMPORTANT)
        alertEvent.setFlightNumber(flight.getFlightNumber());

        return alertEvent;
    }

    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    public List<Alert> getAlertsByFlightNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return flight.getAlerts();
    }

    public void deleteAlertById(Long id) {
        alertRepository.deleteById(id);
    }
}
