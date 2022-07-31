package com.solera.global.qa.mockservertest.utils;

import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;
import java.util.Map;
import org.mockserver.model.ClearType;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.MockServerTestBase;

@Component
public class MockServerUtils extends MockServerTestBase {

  public void createOpenApiExpectation() {
    client
        .upsert(openAPIExpectation(OPEN_API_URL));
  }

  public void createOpenApiExpectation(Map<String, String> operationsAndResponses) {
    client
        .upsert(
            openAPIExpectation(OPEN_API_URL, operationsAndResponses));
  }

  public void createOpenApiExpectation(String operationId, Integer statusCode, Object body)
      throws Exception {
    String bodyString = new ObjectMapper().writeValueAsString(body);
    client
        .when(openAPI(OPEN_API_URL, operationId))
        .respond(response().withStatusCode(statusCode)
            .withHeader(new Header("Content-Type", "application/json"))
            .withBody(bodyString));
  }

  public void reset() {
    client
        .reset();
  }

  public void clear(String path, ClearType clearType) {
    client
        .clear(
            request()
                .withPath(path),
            clearType);
  }

  public void verify(String path, int times) {
    client
        .verify(
            request()
                .withPath(path),
            VerificationTimes.atLeast(times));
  }

  public void verify(String path, String method) {
    client
        .verify(
            request()
                .withPath(path)
                .withMethod(method));
  }

  public void verifyZero() {
    client
        .verifyZeroInteractions();
  }

}
