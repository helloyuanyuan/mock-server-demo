package com.solera.global.qa.mockservertest;

import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.common.PropertyUtils;
import io.restassured.http.ContentType;
import java.util.Map;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;

public class MockServerTestBase {

  protected static final String HOST = PropertyUtils.getInstance().getProperty("host");
  protected static final int PORT =
      Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  protected static final String OPEN_API_URL =
      PropertyUtils.getInstance().getProperty("openapiurl");
  protected static final String MOCK_SERVER_URL = PropertyUtils.getInstance().getUrl(HOST, PORT);
  protected static final String AUTH_HEADER = "Authorization";
  protected static final MockServerClient CLIENT = new MockServerClient(HOST, PORT);

  protected static Header header() {
    return new Header("Content-Type", ContentType.JSON.toString());
  }

  protected static String body(Object value) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(value);
  }

  protected static void createOpenApiExpectation() {
    CLIENT.upsert(openAPIExpectation(OPEN_API_URL));
  }

  protected static void createOpenApiExpectation(Map<String, String> operationsAndResponses) {
    CLIENT.upsert(openAPIExpectation(OPEN_API_URL, operationsAndResponses));
  }

  protected static void createOpenApiExpectation(
      String operationId, Integer statusCode, Object body) throws Exception {
    String bodyString = new ObjectMapper().writeValueAsString(body);
    CLIENT
        .when(openAPI(OPEN_API_URL, operationId))
        .respond(
            response()
                .withStatusCode(statusCode)
                .withHeader(new Header("Content-Type", "application/json"))
                .withBody(bodyString));
  }

  protected static void resetMockServer() {
    CLIENT.reset();
  }

  protected static void clearMockServer(String path, ClearType clearType) {
    CLIENT.clear(request().withPath(path), clearType);
  }

  protected static void verifyTimes(String path, int times) {
    CLIENT.verify(request().withPath(path), VerificationTimes.atLeast(times));
  }

  protected static void verifyMethod(String path, String method) {
    CLIENT.verify(request().withPath(path).withMethod(method));
  }

  protected static void verifyZero() {
    CLIENT.verifyZeroInteractions();
  }
}
