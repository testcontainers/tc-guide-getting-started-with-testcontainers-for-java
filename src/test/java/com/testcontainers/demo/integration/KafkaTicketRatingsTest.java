package com.testcontainers.demo.integration;


import static com.testcontainers.demo.util.KafkaRecordsReader.getOffsets;
import static com.testcontainers.demo.util.KafkaRecordsReader.readRecords;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

import com.testcontainers.demo.config.BaseRestAssuredIntegrationTest;
import com.testcontainers.demo.config.KafkaContainerConfig;
import com.testcontainers.demo.config.PgContainerConfig;
import com.testcontainers.demo.entity.Ticket;
import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.util.KafkaRecordsReader;
import io.restassured.response.ValidatableResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.KafkaContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = {KafkaContainerConfig.class, PgContainerConfig.class}
)
public class KafkaTicketRatingsTest extends BaseRestAssuredIntegrationTest {

    @Autowired
    private KafkaContainer kafka;

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @Test
    public void addTicket() {
        given(requestSpecification).body(new Ticket(null, "ticket Title", "ticket description", null, null, "Open"))
            .when()
            .post("/api/ticket")
            .then()
            .statusCode(201);
    }

    @Test
    public void getAllTickets() {
        given(requestSpecification).body(new Ticket(null, "Title all tickets", "ticket description", null, null, "Open"))
            .when()
            .post("/api/ticket")
            .then()
            .statusCode(201);

        given(requestSpecification)
            .when()
            .get("/api/tickets")
            .then()
            .statusCode(200)
            .body("size()", not(0))
            .body("title", hasItem("Title all tickets"));
    }

    @Test
    public void resolveTicket() {
        int ticketId;
        // add ticket
        String location = given(requestSpecification).body(new Ticket(null, "Ticket Title closed", "ticket description", null, null, "Open"))
            .when()
            .post("/api/ticket")
            .then()
            .extract().response().getHeader("Location");
        ticketId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        given(requestSpecification)
            .when()
            .put("/api/ticket/resolve/{ticketId}", ticketId)
            .then()
            .statusCode(200);

        given(requestSpecification)
            .when()
            .get("/api/ticket/{ticketId}", ticketId)
            .then()
            .body("status", is("RESOLVED"));
    }

    @Test
    public void addTicketRating() {
        //add ticket
        int ticketId;
        String location = given(requestSpecification).body(new Ticket(null, "ticket Title", "ticket description", null, null, "Open"))
            .when()
            .post("/api/ticket")
            .then().extract().response().getHeader("Location");
        // http://localhost:8080/ticket/1
        ticketId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        //resolve ticket
        given(requestSpecification).when().put("/api/ticket/resolve/{ticketId}", ticketId).then().statusCode(200);

        //add rating to ticket
        String comment = "rating comment";
        given(requestSpecification)
            .body(new Rating(ticketId, comment, 5))
            .when()
            .post("api/ratings/add")
            .then()
            .statusCode(201);

        await()
            .untilAsserted(() -> {
                List<ConsumerRecord<Rating, Rating>> records = getRecordsFromTopic();
                Assertions.assertThat(records.size()).isNotZero();
                Assertions.assertThat(records)
                    .allMatch(kafkaRating ->
                        kafkaRating.value().getTicketId().equals(ticketId));
                Assertions.assertThat(records).anyMatch(record ->
                    record.value().getComment().equals(comment));
            });

        // add multiple comments to same ticket
        List<String> comments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            comments.add(comment + i);
            given(requestSpecification)
                .body(new Rating(ticketId, comment + i, i))
                .when()
                .post("api/ratings/add");
        }

        await()
            .untilAsserted(() -> {
                List<ConsumerRecord<Rating, Rating>> ratingsFromKafka = getRecordsFromTopic();
                Assertions.assertThat(ratingsFromKafka.size()).isNotZero();
                Assertions.assertThat(ratingsFromKafka)
                    .allMatch(kafkaRating ->
                        kafkaRating.value().getTicketId().equals(ticketId));
                Assertions.assertThat(
                        ratingsFromKafka.stream().map(ratingsRecords ->
                            ratingsRecords.value().getComment())
                            .collect(Collectors.toList()))
                    .containsAll(comments);
            });

        //retrieve
        await()
            .untilAsserted(() -> {
                ValidatableResponse validatableResponse = given(requestSpecification)
                    .queryParam("ticketId", ticketId)
                    .when()
                    .get("api/ratings")
                    .then();
                validatableResponse.body("ticketId", everyItem(is(ticketId)));
                for (int i = 0; i < 5; i++) {
                    validatableResponse.body("comment", hasItem(comments.get(i)));
                }
            });
    }

    /*
     * Reads the records from the topic "ratings" and returns them  as a list of ConsumerRecord
     * @return a list of ConsumerRecord
     */
    @NotNull
    private List<ConsumerRecord<Rating, Rating>> getRecordsFromTopic() {
        KafkaRecordsReader.setBootstrapServers(kafka.getBootstrapServers());
        final Map<TopicPartition, KafkaRecordsReader.OffsetInfo> partitionOffsetInfos = getOffsets(List.of("ratings"));
        return readRecords(partitionOffsetInfos);
    }

//    /**
//     * This method is not working because we cannot find kafka-console-consumer.sh
//     */
//    private void getInfoFromKafka() {
//        try {
//            Container.ExecResult result = kafka.execInContainer("/usr/bin/kafka-console-consumer.sh", "--bootstrap-server", "localhost:9092", "--topic", "ratings", "--from-beginning");
//            System.out.println(result.getStdout());
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    public void addRatingWithoutTicket() {
        int ticketId = 6;

        given(requestSpecification)
            .body(new Rating(ticketId, "rating comment", 5))
            .when()
            .post("api/ratings/add")
            .then()
            .statusCode(404);
    }

    @Test
    public void healthy() {
        given(requestSpecification)
            .when()
            .get("/actuator/health")
            .then()
            .statusCode(200);
    }
}
