package com.solera.global.qa.mockservertest.common.junitExtension;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.Header;
import org.openapitools.client.model.SkillResult;

public class SchedulingApiV2SkillExtension extends MockServerTestBase
    implements BeforeTestExecutionCallback {

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();

    SkillResult result = new SkillResult();
    result.setId(200);
    result.setName("Driveable");

    Header header = new Header("Content-Type", "application/json");
    String resultString = new ObjectMapper().writeValueAsString(result);

    client
        .when(openAPI(OPENAPIURL, "Skill.List"))
        .respond(
            httpRequest -> {
              if (httpRequest.getHeader("Authorization").get(0).contains("200")) {
                return response().withStatusCode(200).withHeaders(header).withBody(resultString);
              }
              if (httpRequest.getHeader("Authorization").get(0).contains("401")) {
                return response().withStatusCode(401).withHeaders(header);
              } else {
                return notFoundResponse();
              }
            });
  }
}
