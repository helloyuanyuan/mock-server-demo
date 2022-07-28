package com.example.mockserverdemo.utils;

import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;
import java.util.Map;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MockServerUtils {

  public final String HOST = PropertyUtils.getInstance().getProperty("host");
  public final int PORT = Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  public final String OPEN_API_URL =
      PropertyUtils.getInstance().getProperty("open_api_url");

  public final MockServerClient client = new MockServerClient(HOST, PORT);

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
            .withHeader(new Header("Content-Type", "application/json")).withBody(bodyString));
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
