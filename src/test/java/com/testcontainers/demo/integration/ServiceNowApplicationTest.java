package com.testcontainers.demo.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.testcontainers.demo.config.ContainerConfig;
import com.testcontainers.demo.entity.Application;
import io.restassured.filter.log.LogDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = { ContainerConfig.class }
)
public class ServiceNowApplicationTest extends BaseRestAssuredIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceNowApplicationTest.class);

    @Autowired
    private PostgreSQLContainer postgresContainer;

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @AfterAll
    public static void tearDown() {
        LOG.info("Test tearDown At: " + java.time.LocalDateTime.now());
    }

//    @AfterEach
//    public void tearDown() {
//        LOG.info("Cleaning up the database");
//        ensureCleanDB();
//    }

    /*
     * Ensure the database is clean before running the tests
     */
//    private void ensureCleanDB() throws IOException, InterruptedException {
//        Container.ExecResult result = postgresContainer.execInContainer("psql", "-U", "test", "-d", "test", "-c", "TRUNCATE applications, release, ticket CASCADE;");
//        LOG.info(result.getExitCode());
//    }
//    private void ensureCleanDB() {
//        jdbcTemplate.execute("TRUNCATE applications, release, ticket CASCADE;");
//    }

    /**
     * Test case to add an application.
     * Sends a POST request with an application body and expects a status code of 201 or 409.
     */
    @Test
    public void addApplication() {
        given(requestSpecification)
            .body(new Application(null, "Test Application", "Kate Williams", "A test application."))
            .when()
            .post("/snow/application")
            .then()
            .statusCode(oneOf(201, 409));
    }

    /**
     * Test case to verify the functionality of finding an application.
     * Adds an application and then sends a GET request to find it.
     * Expects the body of the response to match the added application.
     */
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
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to update an application.
     * Adds an application and then sends a PUT request with an updated application body.
     * Expects a status code of 200.
     */
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

    /**
     * Test case to verify the deletion of an application.
     * Finds an application and then sends a DELETE request to remove it.
     * Expects a status code of 204.
     */
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
