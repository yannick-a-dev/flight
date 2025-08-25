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


    private static final String PASSWORD_NUMBER_FIELD = "passwordNumber";
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";

    private final AlertRepository alertRepository;
    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public AlertService(FlightRepository flightRepository, AlertRepository alertRepository, KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.flightRepository = flightRepository;
        this.alertRepository = alertRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retryable(value = {SerializationException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Alert createAlertForPassenger(Passenger passenger, Flight flight, LocalDateTime alertDate, String message, String severity) {
        MDC.put("passengerId", passenger != null ? passenger.getId().toString() : "unknown");
        try {
            log.debug("Starting to create alert for Passenger: {}, Flight: {}, AlertDate: {}, Message: {}, Severity: {}",
                    passenger != null ? "[REDACTED]" : null, flight, alertDate, message, severity);

            log.debug("Validating inputs...");
            validateInputs(passenger, flight, alertDate, message, severity);
            log.debug("Inputs validation successful.");

            Alert existingAlert = alertRepository.findByPassengerAndFlightAndMessageAndAlertDate(passenger, flight, message, alertDate);
            if (existingAlert != null) {
                log.info("Existing alert found: {}", existingAlert);
                return existingAlert;
            }

            Severity severityEnum = parseSeverity(severity);
            Alert alert = new Alert(passenger, flight, message, severityEnum, alertDate);
            log.info("New Alert created: {}", alert);

            AlertEvent alertEvent = createAlertEvent(passenger);
            sendAlertEventToKafka(alertEvent);

            Alert savedAlert = alertRepository.save(alert);
            log.info("Alert saved successfully: {}", savedAlert);

            return savedAlert;
        } finally {
            MDC.clear(); // Toujours nettoyer le MDC après l'exécution
        }
    }

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
            // Stocker l'erreur dans un mécanisme de secours (ex: base de données, DLT)
        }
    }

    public GenericRecord createAvroRecord(AlertEvent alertEvent) {
        GenericRecord record = new GenericData.Record(SCHEMA);
        record.put("passwordNumber", alertEvent.getPasswordNumber());
        record.put("email", alertEvent.getEmail());
        record.put("firstName", alertEvent.getFirstName());
        record.put("lastName", alertEvent.getLastName());
        return record;
    }

//    private void fillRecordWithAlertEvent(GenericRecord record, AlertEvent alertEvent) {
//        record.put(EMAIL_FIELD, Optional.ofNullable(alertEvent.getEmail()).orElse(""));
//        record.put(PASSWORD_NUMBER_FIELD, Optional.ofNullable(alertEvent.getPasswordNumber()).orElse(""));
//        record.put(FIRST_NAME_FIELD, Optional.ofNullable(alertEvent.getFirstName()).orElse(""));
//        record.put(LAST_NAME_FIELD, Optional.ofNullable(alertEvent.getLastName()).orElse(""));
//    }
//
//    private String safeToString(Object value) {
//        if (value == null) {
//            log.warn("Null value encountered for field. Returning 'N/A'.");
//            return "N/A";
//        }
//        return value.toString();
//    }

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

    private AlertEvent createAlertEvent(Passenger passenger) {
        AlertEvent alertEvent = new AlertEvent();
        alertEvent.setPasswordNumber(passenger.getPassportNumber());
        alertEvent.setEmail(passenger.getEmail());
        alertEvent.setFirstName(passenger.getFirstName());
        alertEvent.setLastName(passenger.getLastName());
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
