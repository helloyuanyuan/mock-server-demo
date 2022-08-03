package com.solera.global.qa.mockservertest.cases.audaTarget.scheduling.v2;

import static io.restassured.RestAssured.given;

import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.common.annotations.Duration;
import com.solera.global.qa.mockservertest.common.utils.LifecycleLogger;
import com.solera.global.qa.mockservertest.enums.AuthHeader;
import com.solera.global.qa.mockservertest.expecations.audatarget.scheduling.v2.DayGetAllV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.client.model.DayResViewModel;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("audatarget.scheduling.v2.DayGetAllV2")
@Duration
@ExtendWith(DayGetAllV2.class)
class TestDayGetAllV2 extends MockServerTestBase implements LifecycleLogger {

  private final String API_PATH = MOCK_SERVER_URL + "/api/v2/Day/GetAll";

  @Test
  void testGetStatusCode200() throws Exception {
    DayResViewModel actualResult =
        given()
            .log()
            .all()
            .header(AuthHeader.OK_200.header())
            .then()
            .log()
            .all()
            .expect()
            .statusCode(200)
            .when()
            .get(API_PATH)
            .as(DayResViewModel.class);
    Assertions.assertThat(actualResult).isEqualTo(DayGetAllV2.getDayResViewModel());
  }

  @Test
  void testGetStatusCode401() throws Exception {
    given()
        .log()
        .all()
        .header(AuthHeader.UNAUTHORIZED_401.header())
        .then()
        .log()
        .all()
        .expect()
        .statusCode(401)
        .when()
        .get(API_PATH);
  }

  @Test
  void testGetStatusCode404() throws Exception {
    given()
        .log()
        .all()
        .header(AuthHeader.NOT_FOUND_404.header())
        .then()
        .log()
        .all()
        .expect()
        .statusCode(404)
        .when()
        .get(API_PATH);
  }

  @Test
  void testGetStatusCode500() throws Exception {
    given()
        .log()
        .all()
        .header(AuthHeader.INTERNAL_SERVER_ERROR_500.header())
        .then()
        .log()
        .all()
        .expect()
        .statusCode(500)
        .when()
        .get(API_PATH);
  }
}
