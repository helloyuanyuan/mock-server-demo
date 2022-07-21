package com.example.mockserverdemo.utils;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;
import org.springframework.stereotype.Component;

@Component
public class MockServerUtils {

  private final String HOST = PropertyUtils.getInstance().getHost();
  private final int PORT = Integer.parseInt(PropertyUtils.getInstance().getPort());

  public MockServerClient mockServerClient() {
    return new MockServerClient(HOST, PORT);
  }

  public void reset() {
    mockServerClient().reset();
  }

  public void clear(String path, ClearType clearType) {
    mockServerClient().clear(
        request()
            .withPath(path),
        clearType);
  }

  public void verify(String path, int times) {
    mockServerClient()
        .verify(
            request()
                .withPath(path),
            VerificationTimes.atLeast(times));
  }

  public void createGetExpectation(String path, int statusCode, String rspBody) {
    mockServerClient()
        .when(request().withMethod("GET").withPath(path))
        .respond(response().withStatusCode(statusCode).withBody(rspBody));
  }

  public void createGetExpectation(String path, String paramName,
      String paramValue, int statusCode, Header rspHeader, String rspBody) {
    mockServerClient()
        .when(request().withMethod("GET").withPath(path)
            .withQueryStringParameter(paramName, paramValue))
        .respond(response().withStatusCode(statusCode)
            .withHeader(rspHeader).withBody(rspBody));
  }

  public void createPostExpectation(String path, Header reqHeader, String reqBody, int statusCode,
      Header rspHeader, String rspBody) {
    mockServerClient()
        .when(request().withMethod("POST").withPath(path)
            .withHeader(reqHeader).withBody(reqBody))
        .respond(response().withStatusCode(statusCode)
            .withHeader(rspHeader).withBody(rspBody));
  }

}
