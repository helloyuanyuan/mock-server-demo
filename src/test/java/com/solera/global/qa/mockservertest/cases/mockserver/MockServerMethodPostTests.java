package com.solera.global.qa.mockservertest.cases.mockserver;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.beans.Org;
import com.solera.global.qa.mockservertest.utils.junit.extension.TimingExtension;
import com.solera.global.qa.mockservertest.utils.junit.logger.LifecycleLogger;
import io.restassured.http.ContentType;

@SpringBootTest
@DisplayName("MockServerMethodPostTests")
@ExtendWith(TimingExtension.class)
class MockServerMethodPostTests extends MockServerTestBase implements LifecycleLogger {

  @BeforeEach
  void beforeEach() {
    mockServerUtils.reset();
  }

  @Test
  void testPostWithJsonBodyReturnJsonBodyToClass() throws Exception {
    Org org = new Org();
    org.setId("1");
    org.setOrgName("solera");

    String body = new ObjectMapper().writeValueAsString(org);

    client
        .when(request().withMethod("POST").withPath("/org/create")
            .withHeader(new Header("Content-Type", "application/json")).withBody(body))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json")).withBody(body));

    Org actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .body(org)
        .then().log().all().statusCode(200)
        .when().post(URL + "/org/create").as(Org.class);

    Assertions.assertThat(actualResult.getId()).isEqualTo(org.getId());
    Assertions.assertThat(actualResult.getOrgName()).isEqualTo(org.getOrgName());

    mockServerUtils.verify("/org/create", "POST");
  }

}
