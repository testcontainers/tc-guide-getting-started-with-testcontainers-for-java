package com.testcontainers.demo.rating_module.api;

import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.rating_module.repository.RatingsRepository;
import com.testcontainers.demo.service.TicketService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing ratings.
 */
@RestController
@RequestMapping("/ratings")
public class RatingsController {

    private final KafkaTemplate<String, Rating> kafkaTemplate;

    private final RatingsRepository ratingsRepository;

    public RatingsController(
        KafkaTemplate<String, Rating> kafkaTemplate,
        RatingsRepository ratingsRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.ratingsRepository = ratingsRepository;
    }

    /**
     * Records a rating by sending it to the Kafka topic "ratings".
     *
     * @param rating the Rating object to be recorded
     * @return a ResponseEntity indicating that the rating has been accepted
     * @throws Exception if an error occurs while sending the rating to the topic
     */
    @PostMapping
    public ResponseEntity<Object> recordRating(@RequestBody Rating rating) throws Exception {
        if (!ratingsRepository.exists(rating.getTicketId())) {
            return ResponseEntity.notFound().build();
        }

        kafkaTemplate.send("ratings", rating).get();
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public Map<Integer, Integer> getRatings(@RequestParam String talkId) {
        return ratingsRepository.findAllByTalkId(talkId);
    }
}
