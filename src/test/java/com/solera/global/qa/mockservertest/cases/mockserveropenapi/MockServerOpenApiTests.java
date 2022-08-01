package com.solera.global.qa.mockservertest.cases.mockserveropenapi;

import static io.restassured.RestAssured.given;

import com.solera.global.qa.mockservertest.MockServerTestBase;
import com.solera.global.qa.mockservertest.beans.SkillResult;
import com.solera.global.qa.mockservertest.common.junitAnnotation.Duration;
import com.solera.global.qa.mockservertest.common.junitLogger.LifecycleLogger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("MockServerOpenApiTests")
@Duration
class MockServerOpenApiTests extends MockServerTestBase implements LifecycleLogger {

  @Test
  void test() throws Exception {

    resetMockServer();

    SkillResult result = new SkillResult(200, "TestSkill");
    createOpenApiExpectation("Skill.List", 200, result);

    SkillResult actualResult =
        given()
            .log()
            .all()
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
}
