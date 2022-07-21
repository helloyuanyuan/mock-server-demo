package com.example.mockserverdemo;

import static io.restassured.RestAssured.given;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mockserverdemo.beans.Org;
import com.example.mockserverdemo.utils.MockServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

@SpringBootTest
class MockServerDemoTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @Test
  void testGet() {
    mockServerUtils.createExpectation("GET", "/hello", 200, "Hello World!");

    String str = given()
        .then().log().all().statusCode(200)
        .when().get("http://localhost:1080/hello").asString();
    Assertions.assertThat(str).isEqualTo("Hello World!");
  }

  @Test
  void testGetWithParam() throws Exception {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");

    String body = new ObjectMapper().writeValueAsString(org);

    mockServerUtils.createExpectation("GET", "/org", 200, "orgName", "solera", body);

    org = given()
        .contentType(ContentType.JSON)
        .queryParam("orgName", "solera")
        .then().defaultParser(Parser.JSON).log().all().statusCode(200)
        .when().get("http://localhost:1080/org").as(Org.class);
    Assertions.assertThat(org.getId()).isEqualTo("1");
    Assertions.assertThat(org.getOrgName()).isEqualTo("solera");
  }

}
