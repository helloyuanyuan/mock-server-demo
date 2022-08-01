package com.solera.global.qa.mockservertest.cases.audaTarget.scheduling.v2;

import static io.restassured.RestAssured.given;

import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.common.junitAnnotation.Duration;
import com.solera.global.qa.mockservertest.common.junitAnnotation.SchedulingApiV2Skill;
import com.solera.global.qa.mockservertest.common.junitLogger.LifecycleLogger;
import com.solera.global.qa.mockservertest.model.SkillResult;
import io.restassured.http.Header;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("AudaTarget.Scheduling.v2.SkillTests")
@Duration
@SchedulingApiV2Skill
class SkillTests extends MockServerTestBase implements LifecycleLogger {

  SkillResult result = new SkillResult(200, "TestSkill");

  @Test
  void testGetStatusCode200() throws Exception {
    SkillResult actualResult =
        given()
            .log()
            .all()
            .header(new Header("Authorization", "Bearer 200abcdef"))
            .then()
            .log()
            .all()
            .expect()
            .statusCode(200)
            .when()
            .get(MOCKSERVERURL + "/api/v2/skills")
            .as(SkillResult.class);
    Assertions.assertThat(actualResult).isEqualTo(result);
  }

  @Test
  void testGetStatusCode401() throws Exception {
    given()
        .log()
        .all()
        .header(new Header("Authorization", "Bearer 401abcdef"))
        .then()
        .log()
        .all()
        .expect()
        .statusCode(401)
        .when()
        .get(MOCKSERVERURL + "/api/v2/skills");
  }
}
