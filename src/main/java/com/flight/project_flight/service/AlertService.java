package com.flight.project_flight.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.project_flight.enums.Severity;
import com.flight.project_flight.event.AlertEvent;
import com.flight.project_flight.models.Alert;
import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.repository.AlertRepository;
import com.flight.project_flight.repository.PassengerRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AlertRepository alertRepository;

    private final PassengerRepository passengerRepository;

    public AlertService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, AlertRepository alertRepository, PassengerRepository passengerRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.alertRepository = alertRepository;
        this.passengerRepository = passengerRepository;
    }

    public Alert createAlertForPassenger(Passenger passenger, Flight flight, LocalDateTime alertDate, String message, String severity) {
        if (passenger == null || flight == null || alertDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger, flight, and alert date must not be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must not be empty");
        }

        Alert existingAlert = alertRepository.findByPassengerAndFlightAndMessageAndAlertDate(passenger, flight, message, alertDate);
        if (existingAlert != null) {
            return existingAlert;
        }

        Severity severityEnum;
        try {
            severityEnum = Severity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Invalid severity value: '%s'. Allowed values are: %s", severity, Arrays.toString(Severity.values())),
                    e
            );
        }

        Alert alert = new Alert();
        alert.setPassenger(passenger);
        alert.setFlight(flight);
        alert.setMessage(message);
        alert.setSeverity(severityEnum);
        alert.setAlertDate(alertDate);

        // Create an AlertEvent
        AlertEvent alertEvent = new AlertEvent( alert.getPassenger().getPassportNumber(),alert.getPassenger().getEmail());

        try {
            // Convert AlertEvent to JSON string
            String alertEventJson = objectMapper.writeValueAsString(alertEvent);

            log.info("Start - Sending AlertEvent {} to Kafka topic event-placed", alertEventJson);
            kafkaTemplate.send("event-placed", alertEventJson).get(); // Send JSON string to Kafka
            log.info("End - Successfully sent AlertEvent {} to Kafka topic event-placed", alertEventJson);
        } catch (Exception e) {
            log.error("Failed to send AlertEvent to Kafka topic event-placed", e);
            throw new RuntimeException("Failed to send AlertEvent to Kafka", e);
        }

        return alertRepository.save(alert);
    }

    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }
}
