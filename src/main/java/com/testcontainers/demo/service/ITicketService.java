package com.testcontainers.demo.service;

import com.testcontainers.demo.entity.Ticket;
import java.util.List;

public interface ITicketService {
    List<Ticket> getAllTickets();
    Ticket getTicketById(Integer ticketId);
    void addTicket(Ticket ticket);
    void updateTicket(Ticket ticket);
    void deleteTicket(Integer ticketId);
    void resolveTicket(Integer ticketId);
    boolean isTicketResolved(Integer ticketId);
}
