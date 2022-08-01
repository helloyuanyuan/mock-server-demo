package com.solera.global.qa.mockservertest;

import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.OpenAPIDefinition.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solera.global.qa.mockservertest.common.PropertyUtils;
import java.util.Map;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;

public class MockServerTestBase {

  protected final String HOST = PropertyUtils.getInstance().getProperty("host");
  protected final int PORT = Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  protected final String OPENAPIURL = PropertyUtils.getInstance().getProperty("openapiurl");
  protected final String MOCKSERVERURL = PropertyUtils.getInstance().getUrl(HOST, PORT);
  protected final MockServerClient client = new MockServerClient(HOST, PORT);

  public void createOpenApiExpectation() {
    client.upsert(openAPIExpectation(OPENAPIURL));
  }

  public void createOpenApiExpectation(Map<String, String> operationsAndResponses) {
    client.upsert(openAPIExpectation(OPENAPIURL, operationsAndResponses));
  }

  public void createOpenApiExpectation(String operationId, Integer statusCode, Object body)
      throws Exception {
    String bodyString = new ObjectMapper().writeValueAsString(body);
    client
        .when(openAPI(OPENAPIURL, operationId))
        .respond(
            response()
                .withStatusCode(statusCode)
                .withHeader(new Header("Content-Type", "application/json"))
                .withBody(bodyString));
  }

  public void resetMockServer() {
    client.reset();
  }

  public void clearMockServer(String path, ClearType clearType) {
    client.clear(request().withPath(path), clearType);
  }

  public void verifyTimes(String path, int times) {
    client.verify(request().withPath(path), VerificationTimes.atLeast(times));
  }

  public void verifyMethod(String path, String method) {
    client.verify(request().withPath(path).withMethod(method));
  }

  public void verifyZero() {
    client.verifyZeroInteractions();
  }
}
