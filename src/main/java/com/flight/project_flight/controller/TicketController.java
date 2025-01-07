package com.flight.project_flight.controller;

import com.flight.project_flight.models.Ticket;
import com.flight.project_flight.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        // Vérification des relations
        if (ticket.getPassenger() == null || ticket.getFlight() == null) {
            return ResponseEntity.badRequest().build();
        }

        Ticket createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @DeleteMapping("/{ticketNumber}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String ticketNumber) {
        // Check if the ticket with the provided ticketNumber exists
        if (ticketService.existsByTicketNumber(ticketNumber)) {
            ticketService.deleteTicket(ticketNumber);
            return ResponseEntity.noContent().build();  // Successfully deleted, return 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // Ticket not found, return 404 Not Found
        }
    }


    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Ticket>> getTicketsByPassenger(@PathVariable Long passengerId) {
        try {
            List<Ticket> tickets = ticketService.getTicketsByPassenger(passengerId);
            return ResponseEntity.ok(tickets);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
