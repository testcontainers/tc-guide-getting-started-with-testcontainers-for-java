package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public class TicketDAO implements ITicketDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ticket> getAllTickets() {
        String query = "select t from Ticket t order by t.title";
        return (List<Ticket>) entityManager.createQuery(query).getResultList();
    }

    @Override
    public void addTicket(Ticket ticket) {
        entityManager.persist(ticket);
    }

    @Override
    public Ticket getTicketById(int ticketId) {
        return entityManager.find(Ticket.class, ticketId);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        Ticket ticket1 = getTicketById(ticket.getId());

        ticket1.setDescription(ticket.getDescription());
        ticket1.setApplication(ticket.getApplication());
        ticket1.setTitle(ticket.getTitle());

        entityManager.flush();
    }

    @Override
    public void deleteTicket(int ticketId) {
        entityManager.remove(getTicketById(ticketId));
    }

    @Override
    public void resolve(int ticketId) {
        Ticket ticket = getTicketById(ticketId);
        if (ticket!= null) {
            ticket.setStatus("RESOLVED");
        }
    }
}
