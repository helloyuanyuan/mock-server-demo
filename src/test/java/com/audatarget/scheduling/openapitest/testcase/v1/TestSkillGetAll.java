package com.audatarget.scheduling.openapitest.testcase.v1;

import static io.restassured.RestAssured.given;

import com.audatarget.scheduling.MockServerTestBase;
import com.audatarget.scheduling.common.annotations.Duration;
import com.audatarget.scheduling.common.enums.AuthHeader;
import com.audatarget.scheduling.common.utils.LifecycleLogger;
import com.audatarget.scheduling.expecations.v1.SkillGetAll;
import com.audatarget.scheduling.v1.model.SkillResViewModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("audatarget.scheduling.v1.SkillGetAll")
@Duration
@ExtendWith(SkillGetAll.class)
class TestSkillGetAll extends MockServerTestBase implements LifecycleLogger {

  private final String API_PATH = MOCK_SERVER_URL + "/api/v1/Skill/GetAll";

  @Test
  void testGetStatusCode200() throws Exception {
    SkillResViewModel actualResult =
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
            .as(SkillResViewModel.class);
    Assertions.assertThat(actualResult).isEqualTo(SkillGetAll.getSkillResViewModel());
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
