package com.testcontainers.demo.integration;

import com.testcontainers.demo.config.BaseRestAssuredIntegrationTest;
import com.testcontainers.demo.config.PgContainerConfig;
import com.testcontainers.demo.entity.Application;
import com.testcontainers.demo.entity.SoftwareRelease;
import com.testcontainers.demo.service.ApplicationService;
import com.testcontainers.demo.service.SoftwareReleaseService;
import io.restassured.response.ValidatableResponse;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
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

import java.io.IOException;
import java.time.LocalDate;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test",
    },
    classes = {PgContainerConfig.class}
)
public class ApiSoftwareReleaseTest extends BaseRestAssuredIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSoftwareReleaseTest.class);

    public static MockWebServer gitClientMockWebServer;

    @Autowired
    private SoftwareReleaseService softwareReleaseService;

    @Autowired
    private ApplicationService applicationService;

    /*
     * Dispatcher can be used to mock the GitClient service responses by using the query parameters
     */
    final static Dispatcher dispatcher = new Dispatcher() {

        @NotNull
        @Override
        public MockResponse dispatch(RecordedRequest request) {
            assert request.getRequestUrl() != null;
            String releaseDate = request.getRequestUrl().queryParameter("releaseDate");
            String applicationName = request.getRequestUrl().queryParameter("applications");

            if (releaseDate != null && applicationName != null) {
                if (releaseDate.equals("2025-12-31") && applicationName.contains("Test_V1")) {
                    return new MockResponse().setResponseCode(200).setBody("v1.1.2025");
                }
                if (releaseDate.equals("2026-12-31") && applicationName.contains("Test_V2")) {
                    return new MockResponse().setResponseCode(200).setBody("v2.1.2026");
                }
            }
            return new MockResponse().setResponseCode(404);
        }
    };

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @BeforeAll
    static void setUp() throws IOException {
        gitClientMockWebServer = new MockWebServer();
        gitClientMockWebServer.setDispatcher(dispatcher);
        gitClientMockWebServer.start(9091);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        gitClientMockWebServer.shutdown();
    }

    @Test
    public void addRelease() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body("{\"releaseDate\":\"2021-12-31\",\"description\":\"A test release\"}")
            .when()
            .post("/api/softwareRelease")
            .then()
            .statusCode(201)
            .header("Location",  matchesPattern(".*/softwareRelease/\\d+"));

    }

    @Test
    public void addReleaseWithApps() {
        ValidatableResponse validatableResponse = given(requestSpecification)
            .body("{\"releaseDate\":\"2021-12-31\",\"description\":\"A test release\",\"applications\": [{\"name\": \"New App1\", \"description\": \"App added with release\", \"owner\": \"Jane Doe\"},{\"name\": \"New App2\", \"description\": \"Another app added with release\", \"owner\": \"Jane Doe\"}]}")
            .when()
            .post("/api/softwareRelease")
            .then()
            .statusCode(201)
            .header("Location",  matchesPattern(".*/softwareRelease/\\d+"));
    }

    /**
     * Test case to find a release by id.
     * Sends a GET request to find the added release in test seed.
     * Expects the body of the response to match the added release and contain the git Tag from the mock client.
     */
    @Test
    public void findReleaseWithTagsFromGit() {
        // create an application
        Integer appId = applicationService.addApplication(new Application(null, "Test_V1", "Kesha Williams", "A test application.")).getId();
        // create a release
        Integer releaseId = softwareReleaseService.addRelease(new SoftwareRelease(null, LocalDate.of(2025, 12, 31), "A test release", null));

        // link application to release
        given(requestSpecification)
            .when()
            .put("/api/softwareRelease/{appId}/{rId}", appId, releaseId)
            .then()
            .statusCode(200);

//        gitClientMockWebServer.enqueue(new MockResponse().setBody(RELEASE_TAG)); //TODO add URL to work in parallel and to assure that this URL will be called by the test;
        // did not find a solution

        given(requestSpecification)
            .when()
            .get("/api/softwareRelease/{releaseId}", releaseId)
            .then()
            .assertThat()
            .statusCode(200)
            .body("description", is("A test release"))
            .body("releaseDate", is("2025-12-31"))
            .body("gitTags", contains("v1.1.2025"));
    }

    @Test
    public void findRelease2WithTagsFromGit() {
        // create an application
        Integer appId = applicationService.addApplication(new Application(null, "Test_V2", "Kesha Williams", "A test application.")).getId();
        // create a release
        Integer releaseId = softwareReleaseService.addRelease(new SoftwareRelease(null, LocalDate.of(2026, 12, 31), "Test V2 release", null));

        // link application to release
        given(requestSpecification)
            .when()
            .put("/api/softwareRelease/{appId}/{rId}", appId, releaseId)
            .then()
            .statusCode(200);

        given(requestSpecification)
            .when()
            .get("/api/softwareRelease/{releaseId}", releaseId)
            .then()
            .assertThat()
            .statusCode(200)
            .body("description", is("Test V2 release"))
            .body("releaseDate", is("2026-12-31"))
            .body("gitTags", contains("v2.1.2026"));
    }

    @Test
    public void findReleaseWithIncompleteData() {
         // create a release
        Integer releaseId = softwareReleaseService.addRelease(new SoftwareRelease(null, LocalDate.of(2021, 12, 31), "A test release", null));
        given(requestSpecification)
            .when()
            .get("/api/softwareRelease/{releaseId}", releaseId)
            .then()
            .assertThat()
            .statusCode(200)
            .body("description", is("A test release"))
            .body("gitTags", empty());
    }

    /**
     * Check the health of the application instance that was started using the testcontainers configuration for the API case
     */
    @Test
    public void healthy() {
        given(requestSpecification)
            .when()
            .get("/actuator/health")
            .then()
            .statusCode(200);
    }
}
