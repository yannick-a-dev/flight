package com.flight.project_flight.service;

import com.flight.project_flight.models.Alert;
import com.flight.project_flight.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createAlert(Alert alert) {
        return alertRepository.save(alert);
    }
}
