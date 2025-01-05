package com.flight.project_flight.service;

import com.flight.project_flight.models.Flight;
import com.flight.project_flight.models.Passenger;
import com.flight.project_flight.models.Ticket;
import com.flight.project_flight.repository.FlightRepository;
import com.flight.project_flight.repository.PassengerRepository;
import com.flight.project_flight.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;

    private final PassengerRepository passengerRepository;

    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository, PassengerRepository passengerRepository) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        if (ticket.getPassenger() != null && ticket.getFlight() != null) {
            // Assurez-vous que les passagers et vols existent
            Passenger passenger = passengerRepository.findById(ticket.getPassenger().getId()).orElseThrow(() -> new IllegalArgumentException("Passenger not found"));
            Flight flight = flightRepository.findById(ticket.getFlight().getFlightNumber()).orElseThrow(() -> new IllegalArgumentException("Flight not found"));

            ticket.setPassenger(passenger);
            ticket.setFlight(flight);
        }
        return ticketRepository.save(ticket);
    }


    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByPassenger(Long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new EntityNotFoundException("Passenger with id " + passengerId + " not found");
        }
        return ticketRepository.findByPassengerId(passengerId);
    }

    public boolean existsByTicketNumber(String ticketNumber) {
        return ticketRepository.existsById(ticketNumber); // Use ticketNumber as String
    }

    public void deleteTicket(String ticketNumber) {
        ticketRepository.deleteById(ticketNumber); // Use ticketNumber as String
    }
}
