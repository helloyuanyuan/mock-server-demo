package com.solera.global.qa.mockservertest.common.junitExtension;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;
import org.openapitools.client.model.DayResViewModel;
import org.openapitools.client.model.Skill;
import shaded_package.org.apache.http.entity.ContentType;

public class SchedulingApiV2DayGetAllExtension extends MockServerTestBase
    implements BeforeTestExecutionCallback {

  public static final DayResViewModel getDefaultDayResViewModel() {
    DayResViewModel dayResViewModel = new DayResViewModel();
    dayResViewModel.setId(200);
    dayResViewModel.setName(Skill.DRIVEABLE.toString());
    return dayResViewModel;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    resetMockServer();
    Header header = new Header("Content-Type", ContentType.APPLICATION_JSON.toString());
    String resultString = new ObjectMapper().writeValueAsString(getDefaultDayResViewModel());
    client
        .when(openAPI(OPENAPIURL))
        .respond(
            httpRequest -> {
              if (httpRequest
                  .getHeader("Authorization")
                  .get(0)
                  .contains(HttpStatusCode.OK_200.toString())) {
                return response().withStatusCode(200).withHeaders(header).withBody(resultString);
              }
              if (httpRequest
                  .getHeader("Authorization")
                  .get(0)
                  .contains(HttpStatusCode.UNAUTHORIZED_401.toString())) {
                return response().withStatusCode(401).withHeaders(header);
              }
              if (httpRequest
                  .getHeader("Authorization")
                  .get(0)
                  .contains(HttpStatusCode.INTERNAL_SERVER_ERROR_500.toString())) {
                return response().withStatusCode(500).withHeaders(header);
              } else {
                return notFoundResponse();
              }
            });
  }
}
