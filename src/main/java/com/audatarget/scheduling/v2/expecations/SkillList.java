package com.audatarget.scheduling.v2.expecations;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.audatarget.scheduling.MockServerTestBase;
import com.audatarget.scheduling.v2.model.Skill;
import com.audatarget.scheduling.v2.model.SkillResult;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.HttpStatusCode;

public class SkillList extends MockServerTestBase implements BeforeTestExecutionCallback {

  public static final SkillResult getSkillResult() {
    SkillResult result = new SkillResult();
    result.setId(200);
    result.setName(Skill.DRIVEABLE.toString());
    return result;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();
    CLIENT
        .when(openAPI(V2_OPEN_API_URL, "Skill.List"))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.OK_200.toString())) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(header())
                    .withBody(body(getSkillResult()));
              }
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.UNAUTHORIZED_401.toString())) {
                return response().withStatusCode(401).withHeaders(header());
              } else {
                return notFoundResponse();
              }
            });
  }
}
