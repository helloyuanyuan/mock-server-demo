package com.example.mockserverdemo.utils;

import static org.mockserver.model.HttpRequest.request;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.stereotype.Component;

@Component
public class MockServerUtils {

  private static final String HOST = PropertyUtils.getInstance().getHost();
  private static final int PORT = Integer.parseInt(PropertyUtils.getInstance().getPort());

  public static final MockServerClient client = new MockServerClient(HOST, PORT);

  public void reset() {
    client.reset();
  }

  public void clear(String path, ClearType clearType) {
    client.clear(
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
