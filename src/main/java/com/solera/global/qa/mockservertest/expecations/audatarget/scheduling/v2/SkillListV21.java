package com.solera.global.qa.mockservertest.expecations.audatarget.scheduling.v2;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.HttpStatusCode;
import org.openapitools.client.model.Skill;
import org.openapitools.client.model.SkillResult;

public class SkillListV21 extends SkillListV2 {

  public static final SkillResult getSkillResult21() {
    SkillResult result = new SkillResult();
    result.setId(200);
    result.setName(Skill.DRIVEABLE.toString() + "V21");
    return result;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();
    CLIENT
        .when(openAPI(OPEN_API_URL, "Skill.List"))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getHeader(AUTH_HEADER)
                  .get(0)
                  .contains(HttpStatusCode.OK_200.toString())) {
                return response()
                    .withStatusCode(200)
                    .withHeaders(header())
                    .withBody(body(getSkillResult21()));
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
