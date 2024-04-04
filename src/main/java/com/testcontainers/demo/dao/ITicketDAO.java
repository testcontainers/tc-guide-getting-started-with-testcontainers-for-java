package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Ticket;
import java.util.List;

public interface ITicketDAO {
    List<Ticket> getAllTickets();
    void addTicket(Ticket ticket);
    Ticket getTicketById(int ticketId);
    void updateTicket(Ticket ticket);
    void deleteTicket(int ticketId);
    void resolve(int ticketId);
}
