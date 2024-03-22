package com.testcontainers.demo.integration;


import static com.testcontainers.demo.util.KafkaRecordsReader.getOffsets;
import static com.testcontainers.demo.util.KafkaRecordsReader.readRecords;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

import com.testcontainers.demo.ContainerConfig;
import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.util.KafkaRecordsReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.KafkaContainer;

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
public class RatingsKafkaApplicationTest {

    protected RequestSpecification requestSpecification;

    @Autowired
    private KafkaContainer kafka;

    @LocalServerPort
    protected int localServerPort;

    @BeforeEach
    public void setUpAbstractIntegrationTest() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpecification =
            new RequestSpecBuilder()
                .setPort(localServerPort)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Test
    public void testRatings() {
        int ticketId = 1;

        given(requestSpecification)
            .body(new Rating(ticketId,"rating comment", 5))
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
                    .queryParam("talkId", ticketId)
                    .when()
                    .get("/ratings")
                    .then()
                    .body("1", is(1))
                    .log()
                    .everything();
            });

        // try to connect directly to kafka instance to verify that the message was sent;
        // getInfoFromKafka();
        final List<ConsumerRecord<byte[], byte[]>> records = getRecordsFromTopic();
        System.out.println("Read : " + records.size() + " records" + records);
        assert records.size() == 1;
        Assertions.assertThat(records.size()).isNotZero();
        for (int i = 1; i <= 5; i++) {
            given(requestSpecification)
                    .body(new Rating(ticketId, String.valueOf(i), i))
                    .when()
                    .post("/ratings");
        }

        await()
            .untilAsserted(() -> {
                given(requestSpecification)
                    .queryParam("talkId", ticketId)
                    .when()
                    .get("/ratings")
                    .then()
                    .body("1", is(1))
                    .log()
                    .everything();
            });
    }

    /*
     * This method reads the records from the topic "ratings" and returns them  as a list of ConsumerRecord
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
    public void testUnknownTalk() {
        int ticketId = 6;

        given(requestSpecification)
            .body(new Rating(ticketId,"rating comment", 5))
            .when()
            .post("/ratings")
            .then()
            .statusCode(404);
    }
}
