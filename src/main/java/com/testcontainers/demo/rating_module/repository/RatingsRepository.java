package com.testcontainers.demo.rating_module.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingsRepository implements IRatingsRepository {

    public RatingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Integer, Integer> findAllByTalkId(String talkId) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        return map;
    }

    @Override
    public void add(Integer ticketId, int value) {
        jdbcTemplate.update("INSERT INTO ratings (ticketId, value) VALUES (?, ?)", ticketId, value);
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
