package com.testcontainers.demo.integration;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.testcontainers.demo.ContainerConfig;
import com.testcontainers.demo.rating_module.domain.Rating;
import com.testcontainers.demo.service.TicketService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = { ContainerConfig.class }
)
public class RatingsKafkaApplicationTest {

    protected RequestSpecification requestSpecification;

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
