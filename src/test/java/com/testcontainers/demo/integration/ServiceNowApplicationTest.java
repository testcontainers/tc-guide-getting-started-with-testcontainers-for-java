package com.testcontainers.demo.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.testcontainers.demo.ContainerConfig;
import com.testcontainers.demo.entity.Application;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = { ContainerConfig.class }
)
public class ServiceNowApplicationTest {

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
    public void addApplication() {
        given(requestSpecification)
            .body(new Application(null, "Test Application", "Kesha Williams", "A test application."))
            .when()
            .post("/snow/application")
            .then()
            .statusCode(oneOf(201, 409));
    }

    @Test
    public void findApplication() {
        addApplication();

        given(requestSpecification)
            .when()
            .get("/snow/application/1")
            .then()
            .body("id", is(1))
            .body("name", is("Test Application"))
            .body("description", is("A test application."))
            .body("owner", is("Kesha Williams"));
    }

    @Test
    public void updateApplication() {
        addApplication();
        given(requestSpecification)
            .body(new Application(1, "Updated Application", "John Doe", "An updated application."))
            .when()
            .put("/snow/application")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void deleteApplication() {
        findApplication();
        given(requestSpecification)
            .when()
            .delete("/snow/application/1")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
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
}