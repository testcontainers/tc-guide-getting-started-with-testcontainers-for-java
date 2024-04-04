package com.testcontainers.demo.service;

import com.testcontainers.demo.dao.ITicketDAO;
import com.testcontainers.demo.entity.Ticket;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService implements ITicketService {

    @Autowired
    private ITicketDAO ticketDAO;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    @Override
    public Ticket getTicketById(Integer ticketId) {
        return ticketDAO.getTicketById(ticketId);
    }

    @Override
    public void addTicket(Ticket ticket) {
        ticketDAO.addTicket(ticket);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        ticketDAO.updateTicket(ticket);
    }

    @Override
    public void deleteTicket(Integer ticketId) {
        ticketDAO.deleteTicket(ticketId);
    }

    @Override
    public void resolveTicket(Integer ticketId) {
        ticketDAO.resolve(ticketId);
    }

    /**
     * Check if the ticket is resolved
     *
     * @param ticketId the ticket id
     * @return true if the ticket is resolved, false otherwise
     */
    @Override
    public boolean isTicketResolved(Integer ticketId) {
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket != null) {
            return "RESOLVED".equals(ticket.getStatus());
        }
        return false;
    }
}
