package com.flight.project_flight.service;

import com.flight.project_flight.models.Ticket;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    private final PassengerRepository passengerRepository;

    public TicketService(TicketRepository ticketRepository, PassengerRepository passengerRepository) {
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Ticket with id " + id + " not found");
        }
    }
    
    public boolean existsById(Long id) {
        return ticketRepository.existsById(id);
    }

    public List<Ticket> getTicketsByPassenger(Long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new EntityNotFoundException("Passenger with id " + passengerId + " not found");
        }
        return ticketRepository.findByPassengerId(passengerId);
    }
}
