package com.testcontainers.demo.integration;

import com.testcontainers.demo.config.PgContainerConfig;
import com.testcontainers.demo.entity.Application;
import io.restassured.filter.log.LogDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations and running in sequence
 */
@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = { PgContainerConfig.class },
    args = "--spring.profiles.active=test"
)
public class ServiceNowApplicationSequenceTest extends BaseRestAssuredIntegrationTest {

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    /**
     * Test case to add an application.
     * Sends a POST request with an application body and expects a status code of 201 or 409.
     */
    @Test
    @Order(1)
    public void addApplication() {
        given(requestSpecification)
            .body(new Application(null, "Test Application Sequence", "Kate Williams", "A test application Sequence."))
            .when()
            .post("/snow/application")
            .then()
            .statusCode(oneOf(201, 409));
    }

    /**
     * Test case to verify the functionality of finding an application.
     * Sends a GET request to find the added application in previous test.
     * Expects the body of the response to match the added application.
     */
    @Test
    @Order(2)
    public void findApplication() {
        given(requestSpecification)
            .when()
            .get("/snow/application/1")
            .then()
            .body("id", is(1))
            .body("name", is("Test Application Sequence"))
            .body("description", is("A test application Sequence."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to update an application.
     * Sends a PUT request with body containing an updated application information body.
     * Expects a status code of 200.
     */
    @Test
    @Order(3)
    public void updateApplication() {
        given(requestSpecification)
            .body(new Application(1, "Updated Application", "John Doe", "An updated application."))
            .when()
            .put("/snow/application")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    /**
     * Test case to verify the deletion of an application.
     * Sends a DELETE request to remove the added application from previous test.
     * Expects a status code of 204.
     */
    @Test
    @Order(4)
    public void deleteApplication() {
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
