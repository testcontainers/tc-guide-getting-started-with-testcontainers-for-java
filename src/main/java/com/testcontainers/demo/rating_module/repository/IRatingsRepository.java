package com.testcontainers.demo.rating_module.repository;

import java.util.Map;

public interface IRatingsRepository {
    Map<Integer, Integer> findAllByTalkId(String talkId);

    void add(Integer ticketId, int value);
}
