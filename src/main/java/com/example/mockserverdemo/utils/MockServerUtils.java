package com.example.mockserverdemo.utils;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.stereotype.Component;

@Component
public class MockServerUtils {

  private final String HOST = PropertyUtils.getInstance().getHost();
  private final int PORT = Integer.parseInt(PropertyUtils.getInstance().getPort());

  public void createExpectation(String method, String path, int statusCode, String body) {
    new MockServerClient(HOST, PORT)
        .when(request().withMethod(method).withPath(path))
        .respond(response().withStatusCode(statusCode).withBody(body));
  }

  public void createExpectation(String method, String path, int statusCode, String paramName,
      String paramValue, String body) {
    new MockServerClient(HOST, PORT)
        .when(request().withMethod(method).withPath(path)
            .withHeader(new Header("Content-Type", "application/json"))
            .withQueryStringParameter(paramName, paramValue))
        .respond(response().withStatusCode(statusCode)
            .withBody(body));
  }

}
