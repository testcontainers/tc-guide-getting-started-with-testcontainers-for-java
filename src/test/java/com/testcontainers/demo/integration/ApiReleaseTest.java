package com.testcontainers.demo.integration;

import com.testcontainers.demo.config.PgContainerConfig;
import com.testcontainers.demo.entity.Application;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/*
 * Test class using the approach of having a configuration class with the testcontainers configurations
 */
@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test",
    },
    classes = {PgContainerConfig.class}
)
public class ApiReleaseTest extends BaseRestAssuredIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiReleaseTest.class);

    private static final String RELEASE_TAG = "v1.1.2024";

    public static MockWebServer gitClientMockWebServer;

    /*
     * This is a dispatcher can be be used to mock the GitClient service by using the query parameters
     */
//    final static Dispatcher dispatcher = new Dispatcher() {
//
//        @NotNull
//        @Override
//        public MockResponse dispatch(RecordedRequest request) {
//            assert request.getRequestUrl() != null;
//            String releaseDate = request.getRequestUrl().queryParameter("releaseDate");
//            LOG.info("Request Path: " + request.getRequestUrl().queryParameter("applicationId"));
//            String applicationId = request.getRequestUrl().queryParameter("applicationId");
//            if (releaseDate != null && applicationId != null) {
//                if (releaseDate.equals("2021-12-31") && applicationId.equals("1")) {
//                    return new MockResponse().setResponseCode(200).setBody(RELEASE_TAG);
//                }
//            }
//            return new MockResponse().setResponseCode(404);
//        }
//    };

    @BeforeEach
    public void setUpIntegrationTest() {
        this.setUpAbstractIntegrationTest();
    }

    @BeforeAll
    static void setUp() throws IOException {
        gitClientMockWebServer = new MockWebServer();
//        gitClientMockWebServer.setDispatcher(dispatcher);
        gitClientMockWebServer.start(9091);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        gitClientMockWebServer.shutdown();
    }

    @Test
    public void addRelease() {
        given(requestSpecification)
            .body("{\"releaseDate\":\"2021-12-31\",\"description\":\"A test release\"}")
            .when()
            .post("/snow/release")
            .then()
            .statusCode(201);
    }

    @Test
    public void findReleaseWithTags() {
        given(requestSpecification)
            .body("{\"releaseDate\":\"2021-12-31\",\"description\":\"A test release\"}")
            .when()
            .post("/snow/release");
        given(requestSpecification)
            .body(new Application(null, "Test Application", "Kesha Williams", "A test application."))
            .when()
            .post("/snow/application");

        given(requestSpecification)
            .when()
            .put("/snow/release/1/1")
            .then()
            .statusCode(200);

        gitClientMockWebServer.enqueue(new MockResponse().setBody(RELEASE_TAG));

        given(requestSpecification)
            .when()
            .get("/snow/release/1")
            .then()
            .assertThat()
            .statusCode(200)
            .body("description", is("A test release"))
            .body("gitTags", contains(RELEASE_TAG));
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
            .statusCode(200);
    }
}
