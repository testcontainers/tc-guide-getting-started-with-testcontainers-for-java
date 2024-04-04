package com.testcontainers.demo.rating_module.api;

import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.rating_module.repository.RatingsRepository;

import java.util.List;

import com.testcontainers.demo.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing ratings.
 */
@RestController
@RequestMapping("api/ratings")
public class RatingsController {

    private final KafkaTemplate<String, Rating> kafkaTemplate;

    private final RatingsRepository ratingsRepository;

    private final TicketService     ticketService;

    public RatingsController(
        KafkaTemplate<String, Rating> kafkaTemplate,
        RatingsRepository ratingsRepository,
        TicketService ticketService
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.ratingsRepository = ratingsRepository;
        this.ticketService = ticketService;
    }

    /**
     * Records a rating by sending it to the Kafka topic "ratings".
     *
     * @param rating the Rating object to be recorded
     * @return a ResponseEntity indicating that the rating has been accepted
     * @throws Exception if an error occurs while sending the rating to the topic
     */
    @PostMapping("/add")
    public ResponseEntity<Object> recordRating(@RequestBody Rating rating) throws Exception {
        if (!ticketService.isTicketResolved(rating.getTicketId())) {
            return ResponseEntity.notFound().build();
        }

        kafkaTemplate.send("ratings", rating).get();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public List<Rating> getRatings(@RequestParam Integer ticketId) {
        return ratingsRepository.findAllByTicketId(ticketId);
    }
}
