package com.audatarget.scheduling.expecations.v1;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.audatarget.scheduling.MockServerTestBase;
import com.audatarget.scheduling.v1.model.Skill;
import com.audatarget.scheduling.v1.model.SkillResViewModel;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.HttpStatusCode;

public class SkillGetAll extends MockServerTestBase implements BeforeTestExecutionCallback {

  public static final SkillResViewModel getSkillResViewModel() {
    SkillResViewModel result = new SkillResViewModel();
    result.setId(200);
    result.setSkill(Skill.DRIVE.toString());
    return result;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();
    CLIENT
        .when(openAPI(V1_OPEN_API_URL))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.OK_200.toString())) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(header())
                    .withBody(body(getSkillResViewModel()));
              }
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.UNAUTHORIZED_401.toString())) {
                return response().withStatusCode(401).withHeaders(header());
              }
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.INTERNAL_SERVER_ERROR_500.toString())) {
                return response().withStatusCode(500).withHeaders(header());
              } else {
                return notFoundResponse();
              }
            });
  }
}
