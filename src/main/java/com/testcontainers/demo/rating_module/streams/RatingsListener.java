package com.testcontainers.demo.rating_module.streams;

import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.rating_module.repository.RatingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RatingsListener {

    private static final Logger LOG = LoggerFactory.getLogger(RatingsListener.class);

    private final RatingsRepository ratingsRepository;

    public RatingsListener(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    @KafkaListener(groupId = "ratings", topics = "ratings")
    public void handle(@Payload Rating rating) {
        LOG.info("Received rating: {}", rating.toString());

        ratingsRepository.add(rating.getTicketId(), rating.getComment(), rating.getStars());
    }
}
