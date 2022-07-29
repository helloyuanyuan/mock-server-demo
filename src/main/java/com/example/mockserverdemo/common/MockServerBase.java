package com.example.mockserverdemo.common;

import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.example.mockserverdemo.utils.MockServerUtils;
import com.example.mockserverdemo.utils.PropertyUtils;

public class MockServerBase {

  protected final String HOST = PropertyUtils.getInstance().getProperty("host");
  protected final int PORT = Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  protected final String OPEN_API_URL =
      PropertyUtils.getInstance().getProperty("open_api_url");
  protected final String URL = "http://" + HOST + ":" + PORT;
  protected final MockServerClient client = new MockServerClient(HOST, PORT);

  @Autowired
  @Lazy
  protected MockServerUtils mockServerUtils;

}
