package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Org;
import com.example.mockserverdemo.utils.MockServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerGenericTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @AfterEach
  void tearDown() {
    mockServerUtils.reset();
  }

  @Test
  void testCreateBasicExpectation() {
    MockServerUtils.client.when(request().withPath("/some/path"))
        .respond(response().withBody("some_response_body"));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/some/path").asString();
    Assertions.assertThat(body).isEqualTo("some_response_body");
  }

  @Test
  void testCreateUpdateExpectation() {
    String expectationID = UUID.randomUUID().toString();
    String bodyA = "AAA";
    String bodyB = "BBB";

    MockServerUtils.client
        .when(request().withMethod("GET").withPath("/hello")).withId(expectationID)
        .respond(response().withStatusCode(200).withBody(bodyA));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("AAA");

    MockServerUtils.client
        .when(request().withMethod("GET").withPath("/hello")).withId(expectationID)
        .respond(response().withStatusCode(200).withBody(bodyB));

    body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("BBB");
  }

  @Test
  void testMatchingOrderCreationFirst() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    MockServerUtils.client.when(request().withMethod("GET").withPath("/hello"))
        .respond(response().withStatusCode(200).withBody(bodyA));

    MockServerUtils.client.when(request().withMethod("GET").withPath("/hello"))
        .respond(response().withStatusCode(200).withBody(bodyB));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("AAA");
  }

  @Test
  void testMatchingOrderPriorityFirst() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    MockServerUtils.client.when(request().withMethod("GET").withPath("/hello")).withPriority(1)
        .respond(response().withStatusCode(200).withBody(bodyA));

    MockServerUtils.client.when(request().withMethod("GET").withPath("/hello")).withPriority(2)
        .respond(response().withStatusCode(200).withBody(bodyB));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("BBB");
  }

  @Test
  void testMatchingOrderTimesExactly() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    MockServerUtils.client
        .when(request().withMethod("GET").withPath("/hello"), Times.exactly(1))
        .respond(response().withStatusCode(200).withBody(bodyA));

    MockServerUtils.client
        .when(request().withMethod("GET").withPath("/hello"), Times.exactly(1))
        .respond(response().withStatusCode(200).withBody(bodyB));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("AAA");

    body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(body).isEqualTo("BBB");

    String returnStatus = given().log().all()
        .then().log().all().statusCode(404)
        .when().get("http://localhost:1080/hello").statusLine();
    Assertions.assertThat(returnStatus).contains("Not Found");
  }

  @Test
  void testRegexSyntaxSupport() throws Exception {
    String orgName = RandomStringUtils.randomAlphabetic(10);
    Org org = new Org();
    org.setId(UUID.randomUUID().toString());
    org.setOrgName(orgName);

    String body = new ObjectMapper().writeValueAsString(org);

    MockServerUtils.client
        .when(request().withMethod("GET").withPath("/org")
            .withQueryStringParameter("orgName", "[A-z]{0,10}"))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json")).withBody(body));

    org = given().log().all()
        .queryParam("orgName", orgName)
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/org").as(Org.class);
    Assertions.assertThat(org.getOrgName()).isEqualTo(orgName);
  }

}
