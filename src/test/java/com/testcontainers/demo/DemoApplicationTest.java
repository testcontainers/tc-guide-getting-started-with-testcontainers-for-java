package com.testcontainers.demo;


import com.testcontainers.demo.domain.Rating;
import com.testcontainers.demo.repository.TalksRepository;
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

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
  }, classes = {ContainerConfig.class})
public class DemoApplicationTest {

  @Test
  public void testRatings() {
    String talkId = "testcontainers-integration-testing";

    given(requestSpecification)
      .body(new Rating(talkId, 5))
      .when()
      .post("/ratings")
      .then()
      .statusCode(202);

    await().untilAsserted(() -> {
      given(requestSpecification)
        .queryParam("talkId", talkId)
        .when()
        .get("/ratings")
        .then()
        .body("5", is(1));
    });

    for (int i = 1; i <= 5; i++) {
      given(requestSpecification)
        .body(new Rating(talkId, i))
        .when()
        .post("/ratings");
    }

    await().untilAsserted(() -> {
      given(requestSpecification)
        .queryParam("talkId", talkId)
        .when()
        .get("/ratings")
        .then()
        .body("1", is(1))
        .body("2", is(1))
        .body("3", is(1))
        .body("4", is(1))
        .body("5", is(2));
    });
  }


  @Autowired
  TalksRepository talksRepository;

  @Test
  public void contextLoads() {
    Assertions.assertThat(talksRepository.exists("testcontainers-integration-testing")).isTrue();
  }

//  @Test
//  public void healthy() {
//    given(requestSpecification)
//      .when()
//      .get("/actuator/health")
//      .then()
//      .statusCode(200)
//      .log().ifValidationFails(LogDetail.ALL);
//  }

  @Test
  public void testUnknownTalk() {
    String talkId = "cdi-the-great-parts";

    given(requestSpecification)
      .body(new Rating(talkId, 5))
      .when()
      .post("/ratings")
      .then()
      .statusCode(404);
  }


  protected RequestSpecification requestSpecification;

  @LocalServerPort
  protected int localServerPort;

  @BeforeEach
  public void setUpAbstractIntegrationTest() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    requestSpecification = new RequestSpecBuilder()
      .setPort(localServerPort)
      .addHeader(
        HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_JSON_VALUE
      )
      .build();
  }
}
