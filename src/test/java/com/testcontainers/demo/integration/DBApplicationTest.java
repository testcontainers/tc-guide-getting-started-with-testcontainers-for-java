package com.testcontainers.demo.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.testcontainers.demo.config.BaseRestAssuredIntegrationTest;
import com.testcontainers.demo.config.PgContainerConfig;
import com.testcontainers.demo.entity.Application;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.concurrent.TimeUnit;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test"
    },
    classes = {PgContainerConfig.class}
)
public class DBApplicationTest extends BaseRestAssuredIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(DBApplicationTest.class);
    private static StopWatch watch;

    @Autowired
    private PostgreSQLContainer postgresContainer;

    @BeforeAll
    public static void setUp() {
        LOG.info("Test setUp At: " + java.time.LocalDateTime.now());
        watch = new StopWatch();
        watch.start();
    }

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @AfterAll
    public static void tearDown() {
        watch.stop();
        float time = (float) watch.getTime(TimeUnit.MILLISECONDS);
        LOG.info("Test tearDown At: {}, duration: {}", java.time.LocalDateTime.now(), time / 1000);
    }

    /**
     * Test case to add an application.
     * Sends a POST request with an application body and expects a status code of 201 or 409.
     */
    @Test
    public void addApplication() {
        given(requestSpecification)
            .body(new Application(null, "Test Application add", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201))
            .body("id", notNullValue())
            .body("name", containsString("Test Application add"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to add an application that already exists
     * Sends a POST request with an application body and expects a status code of 201 or 409.
     */
    @Test
    public void addApplicationAlreadyExists() {
        given(requestSpecification)
            .body(new Application(null, "Test Application existing", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201));
        given(requestSpecification)
            .body(new Application(null, "Test Application existing", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(409));
    }

    /**
     * Test case to verify the functionality of finding an application.
     * Adds an application and then sends a GET request to find it.
     * Expects the body of the response to match the added application.
     */
    @Test
    public void findApplication() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application find", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .body("id", is(response.getId()))
            .body("name", containsString("Test Application find"))
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
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application update", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .body(new Application(response.getId(), "Updated Application", "John Doe", "An updated application."))
            .when()
            .put("/api/application")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(response.getId()))
            .body("name", containsString("Updated Application"))
            .body("owner", is("John Doe"))
            .body("description", is("An updated application."));
    }

    /**
     * Test case to verify the deletion of an application.
     * Finds an application and then sends a DELETE request to remove it.
     * Expects a status code of 204.
     */
    @Test
    public void deleteApplication() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application delete", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .when()
            .delete("/api/application/{id}", response.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void addApplication2() {
        given(requestSpecification)
            .body(new Application(null, "Test Application 2 add", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201))
            .body("id", notNullValue())
            .body("name", containsString("Test Application 2 add"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    public void findApplication2() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application find", "Kate Williams", "A test application."))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .body("id", is(response.getId()))
            .body("name", containsString("Test Application find"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to update an application.
     * Adds an application and then sends a PUT request with an updated application body.
     * Expects a status code of 200.
     */
    @Test
    public void updateApplication2() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application 2 update", "Kate Williams", "A test application 2"))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .body(new Application(response.getId(), "Updated Application 2", "John Doe", "An updated application 2"))
            .when()
            .put("/api/application")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    /**
     * Test case to verify the deletion of an application.
     * Finds an application and then sends a DELETE request to remove it.
     * Expects a status code of 204.
     */
    @Test
    public void deleteApplication2() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body(new Application(null, "Test Application 2 delete", "Kate Williams 2", "A test application."))
            .when()
            .post("/api/application")
            .then();
        Application response = validatableResponse.extract().response().body().as(Application.class);

        given(requestSpecification)
            .when()
            .delete("/api/application/{id}", response.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
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
