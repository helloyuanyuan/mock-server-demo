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
@DisplayName("MockServerMethodPutTests")
@ExtendWith(TimingExtension.class)
class MockServerMethodPutTests extends MockServerTestBase implements LifecycleLogger {

  @BeforeEach
  void beforeEach() {
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

    client
        .when(request().withMethod("PUT").withPath("/org/{orgId}")
            .withPathParameter("orgId", "^\\d+$")
            .withHeader(new Header("Content-Type", "application/json")).withBody(body))
        .respond(response().withStatusCode(200)
            .withHeader(new Header("Content-Type", "application/json"))
            .withBody(bodyUpdated));

    Org actualResult = given().log().all()
        .contentType(ContentType.JSON)
        .pathParam("orgId", org.getId())
        .body(org)
        .then().log().all().statusCode(200)
        .when().put(URL + "/org/{orgId}").as(Org.class);

    Assertions.assertThat(actualResult.getId()).isEqualTo(orgUpdated.getId());
    Assertions.assertThat(actualResult.getOrgName()).isEqualTo(orgUpdated.getOrgName());
  }

}
