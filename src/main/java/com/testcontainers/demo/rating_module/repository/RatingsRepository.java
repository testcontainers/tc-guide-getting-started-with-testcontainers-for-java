package com.testcontainers.demo.rating_module.repository;

import java.util.List;

import com.testcontainers.demo.rating_module.domain.Rating;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingsRepository implements IRatingsRepository {

    public RatingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> findAllByTicketId(Integer ticketId) {
        return jdbcTemplate.query(
            "SELECT * FROM ratings WHERE ticketId = ?",
            (row, i) -> new Rating(row.getInt("ticketId"), row.getString("comment"), row.getInt("stars")),
            ticketId
        );
    }

    @Override
    public void add(Integer ticketId, String comment, int stars) {
        jdbcTemplate.update("INSERT INTO ratings (ticketId, comment, stars) VALUES (?, ?, ?)", ticketId, comment, stars);
    }

    public Boolean exists(Integer ticketId) {
        List<Boolean> results = jdbcTemplate.query(
            "SELECT 1 FROM ratings WHERE ticketId = ?",
            (row, i) -> true,
            ticketId
        );
        return !results.isEmpty();
    }
}
