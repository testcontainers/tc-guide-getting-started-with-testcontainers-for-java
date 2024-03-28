package com.testcontainers.demo.integration;


import static com.testcontainers.demo.util.KafkaRecordsReader.getOffsets;
import static com.testcontainers.demo.util.KafkaRecordsReader.readRecords;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

import com.testcontainers.demo.config.ContainerConfig;
import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.util.KafkaRecordsReader;
import io.restassured.filter.log.LogDetail;
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

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = { ContainerConfig.class }
)
public class RatingsKafkaApplicationTest extends BaseRestAssuredIntegrationTest {

    @Autowired
    private KafkaContainer kafka;

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @Test
    public void testRatings() {
        int ticketId = 1;
        String comment = "rating comment";
        given(requestSpecification)
            .body(new Rating(ticketId, comment, 5))
            .when()
            .post("/ratings")
            .then()
            .statusCode(202)
            .log()
            .everything();

        //waits until the requests goes to kafka and is back into our application
        await()
            .untilAsserted(() -> {
                given(requestSpecification)
                    .queryParam("ticketId", ticketId)
                    .when()
                    .get("/ratings")
                    .then()
                    .body("size()", is(3))
                    .log()
                    .everything();
            });

        // try to connect directly to kafka instance to verify that the message was sent;
        // getInfoFromKafka();
        final List<ConsumerRecord<byte[], byte[]>> records = getRecordsFromTopic();
        System.out.println("Read : " + records.size() + " records" + records);
        assert records.size() == 1;
        Assertions.assertThat(records.size()).isNotZero();

        List<String> comments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            comments.add(comment + i);
            given(requestSpecification)
                    .body(new Rating(ticketId,comment +i, i))
                    .when()
                    .post("/ratings");
        }

        await()
            .untilAsserted(() -> {
                ValidatableResponse validatableResponse = given(requestSpecification)
                    .queryParam("ticketId", ticketId)
                    .when()
                    .get("/ratings")
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
    private List<ConsumerRecord<byte[], byte[]>> getRecordsFromTopic() {
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
    public void contextLoads() {
        Assertions.assertThat(localServerPort).isNotZero();
    }

    @Test
    public void healthy() {
        given(requestSpecification)
            .when()
            .get("/actuator/health")
            .then()
            .statusCode(200)
            .log()
            .ifValidationFails(LogDetail.ALL);
    }

    @Test
    public void testUnknownTicket() {
        int ticketId = 6;

        given(requestSpecification)
            .body(new Rating(ticketId,"rating comment", 5))
            .when()
            .post("/ratings")
            .then()
            .statusCode(404);
    }
}
