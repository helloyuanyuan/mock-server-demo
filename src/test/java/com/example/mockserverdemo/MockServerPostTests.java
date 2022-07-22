package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
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

  @AfterAll
  void tearDown() {
    mockServerUtils.reset();
  }

  @Test
  void testPostWithJsonBodyReturnJsonBodyToClass() throws Exception {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");

    String body = new ObjectMapper().writeValueAsString(org);

    mockServerUtils.createPostExpectation("/org/create",
        new Header("Content-Type", "application/json"),
        body, 200,
        new Header("Content-Type", "application/json"), body);

    org = given()
        .contentType(ContentType.JSON)
        .body(org)
        .then().log().all().statusCode(200)
        .when().post("http://localhost:1080/org/create").as(Org.class);
    Assertions.assertThat(org.getId()).isEqualTo("1");
    Assertions.assertThat(org.getOrgName()).isEqualTo("solera");

    mockServerUtils.verify("/org/create", "POST");

  }

}
