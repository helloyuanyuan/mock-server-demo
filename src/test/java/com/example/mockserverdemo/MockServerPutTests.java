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
class MockServerPutTests {

  @Autowired
  MockServerUtils mockServerUtils;

  @BeforeEach
  void tearDown() {
    mockServerUtils.reset();
  }

  @Test
  void testPutWithJsonBodyReturnJsonBodyToClass() throws Exception {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");

    Org orgUpdated = new Org();
    orgUpdated.setId("1");
    orgUpdated.setOrgName("soleraUpdated");

    String body = new ObjectMapper().writeValueAsString(org);
    String bodyUpdated = new ObjectMapper().writeValueAsString(orgUpdated);

    MockServerUtils.client
        .when(request().withMethod("PUT").withPath("/org/{orgId}")
            .withPathParameter("orgId", "^\\d+$")
            .withHeader(new Header("Content-Type", "application/json")).withBody(body))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json")).withBody(bodyUpdated));

    orgUpdated = given().log().all()
        .contentType(ContentType.JSON)
        .pathParam("orgId", org.getId())
        .body(org)
        .then().log().all().statusCode(200)
        .when().put("http://localhost:1080/org/{orgId}").as(Org.class);
    Assertions.assertThat(orgUpdated.getId()).isEqualTo("1");
    Assertions.assertThat(orgUpdated.getOrgName()).isEqualTo("soleraUpdated");
  }

}
