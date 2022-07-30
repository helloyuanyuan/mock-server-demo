package com.solera.global.qa.mockservertest;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.Cookie.cookie;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.beans.Org;
import com.solera.global.qa.mockservertest.common.MockServerBase;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerGenericTests extends MockServerBase {

  @BeforeEach
  void beforeEach() {
    mockServerUtils.reset();
  }

  @Test
  void testCreateExpectation() {
    client
        .when(request().withPath("/some/path"))
        .respond(response().withBody("some_response_body"));

    String actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/some/path").asString();

    Assertions.assertThat(actualResult).isEqualTo("some_response_body");
  }

  @Test
  void testCreateExpectationWithPathParameterRegex() {
    client
        .when(request().withPath("/some/path/{cartId}")
            .withPathParameters(param("cartId", "[A-Z0-9\\-]+")))
        .respond(response().withBody("some_response_body"));

    String actualResult = given().log().all()
        .pathParam("cartId", "A123")
        .then().log().all().statusCode(200)
        .when().get(URL + "/some/path/{cartId}").asString();

    Assertions.assertThat(actualResult).isEqualTo("some_response_body");

    actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/some/path/{cartId}", "B223").asString();

    Assertions.assertThat(actualResult).isEqualTo("some_response_body");
  }

  @Test
  void testCreateExpectationWithQueryParameterRegex() throws Exception {
    String orgName = RandomStringUtils.randomAlphabetic(10);
    Org org = new Org();
    org.setId(UUID.randomUUID().toString());
    org.setOrgName(orgName);

    String body = new ObjectMapper().writeValueAsString(org);

    client
        .when(request().withMethod("GET").withPath("/org")
            .withQueryStringParameter("orgName", "[A-z]{0,10}"))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json")).withBody(body));

    Org actualResult = given().log().all()
        .queryParam("orgName", orgName)
        .then().log().all().statusCode(200)
        .when().get(URL + "/org").as(Org.class);

    Assertions.assertThat(actualResult.getOrgName()).isEqualTo(orgName);
  }

  @Test
  void testCreateExpectationWithHeadersRegex() {
    client
        .when(request().withMethod("GET").withPath("/some/path")
            .withHeaders(
                header("Accept.*")))
        .respond(response().withBody("some_response_body"));

    String actualResult = given().log().all()
        .header(new io.restassured.http.Header("Accept", "application/json"))
        .header(new io.restassured.http.Header("Accept-Encoding", "gzip, deflate, br"))
        .then().log().all().statusCode(200)
        .when().get(URL + "/some/path").asString();

    Assertions.assertThat(actualResult).isEqualTo("some_response_body");
  }

  @Test
  void testCreateExpectationWithCookies() {
    client
        .when(request().withMethod("GET").withPath("/some/path")
            .withCookies(
                cookie("sessionA", "4930456C-C718-476F-971F-CB8E047AB348"),
                cookie("sessionB", "4930456C-C718-476F-971F-CB8E047AB349")))
        .respond(response().withBody("some_response_body"));

    String actualResult = given().log().all()
        .cookies("sessionA", "4930456C-C718-476F-971F-CB8E047AB348",
            "sessionB", "4930456C-C718-476F-971F-CB8E047AB349")
        .then().log().all().statusCode(200)
        .when().get(URL + "/some/path").asString();

    Assertions.assertThat(actualResult).isEqualTo("some_response_body");
  }

  @Test
  void testUpdateExpectation() {
    String expectationID = UUID.randomUUID().toString();
    String bodyA = "AAA";
    String bodyB = "BBB";

    client
        .when(request().withMethod("GET").withPath("/hello")).withId(expectationID)
        .respond(response().withStatusCode(200).withBody(bodyA));

    String actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(actualResult).isEqualTo("AAA");

    client
        .when(request().withMethod("GET").withPath("/hello")).withId(expectationID)
        .respond(response().withStatusCode(200).withBody(bodyB));

    actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(actualResult).isEqualTo("BBB");
  }

  @Test
  void testMatchingOrderCreationFirst() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    client.when(request().withMethod("GET").withPath("/hello"))
        .respond(response().withStatusCode(200).withBody(bodyA));

    client.when(request().withMethod("GET").withPath("/hello"))
        .respond(response().withStatusCode(200).withBody(bodyB));

    String actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(actualResult).isEqualTo("AAA");
  }

  @Test
  void testMatchingOrderPriorityFirst() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    client.when(request().withMethod("GET").withPath("/hello"))
        .withPriority(1)
        .respond(response().withStatusCode(200).withBody(bodyA));

    client.when(request().withMethod("GET").withPath("/hello"))
        .withPriority(2)
        .respond(response().withStatusCode(200).withBody(bodyB));

    String body = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(body).isEqualTo("BBB");
  }

  @Test
  void testMatchingOrderTimesExactly() {
    String bodyA = "AAA";
    String bodyB = "BBB";

    client
        .when(request().withMethod("GET").withPath("/hello"), Times.exactly(1))
        .respond(response().withStatusCode(200).withBody(bodyA));

    client
        .when(request().withMethod("GET").withPath("/hello"), Times.exactly(1))
        .respond(response().withStatusCode(200).withBody(bodyB));

    String actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(actualResult).isEqualTo("AAA");

    actualResult = given().log().all()
        .then().log().all().statusCode(200)
        .when().get(URL + "/hello").asString();

    Assertions.assertThat(actualResult).isEqualTo("BBB");

    actualResult = given().log().all()
        .then().log().all().statusCode(404)
        .when().get(URL + "/hello").statusLine();
    Assertions.assertThat(actualResult).contains("Not Found");
  }

}
