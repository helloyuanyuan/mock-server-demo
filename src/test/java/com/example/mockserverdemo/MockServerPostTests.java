package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Org;
import com.example.mockserverdemo.utils.MockServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class MockServerPostTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @BeforeEach
  void tearDown() {
    mockServerUtils.reset();
  }

  @Test
  void testPostWithJsonBodyReturnJsonBodyToClass() throws Exception {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");

    String body = new ObjectMapper().writeValueAsString(org);

    MockServerUtils.client
        .when(request().withMethod("POST").withPath("/org/create")
            .withHeader(new Header("Content-Type", "application/json")).withBody(body))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json")).withBody(body));

    Org actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .body(org)
        .then().log().all().statusCode(200)
        .when().post("http://localhost:1080/org/create").as(Org.class);

    Assertions.assertThat(actualResult.getId()).isEqualTo(org.getId());
    Assertions.assertThat(actualResult.getOrgName()).isEqualTo(org.getOrgName());

    mockServerUtils.verify("/org/create", "POST");
  }

}
