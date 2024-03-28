package com.testcontainers.demo.rating_module.repository;

import com.testcontainers.demo.rating_module.domain.Rating;

import java.util.List;

public interface IRatingsRepository {
    List<Rating> findAllByTicketId(Integer ticketId);

    void add(Integer ticketId, String comment, int value);
}
