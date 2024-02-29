package com.testcontainers.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RatingsRepository {
    public RatingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final JdbcTemplate jdbcTemplate;

    public Map<Integer, Integer> findAll(String talkId) {
        return new HashMap<>();
    }

    public void add(String talkId, int value) {
        //
    }

    protected String toKey(String talkId) {
        return "ratings/" + talkId;
    }
}
