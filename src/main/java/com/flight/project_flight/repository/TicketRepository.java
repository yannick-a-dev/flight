package com.flight.project_flight.repository;

import com.flight.project_flight.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByPassengerId(Long passengerId);
}
