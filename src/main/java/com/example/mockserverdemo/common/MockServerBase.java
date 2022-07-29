package com.example.mockserverdemo.common;

import org.mockserver.client.MockServerClient;
import com.example.mockserverdemo.utils.PropertyUtils;

public class MockServerBase {

  public final String HOST = PropertyUtils.getInstance().getProperty("host");
  public final int PORT = Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  public final String OPEN_API_URL =
      PropertyUtils.getInstance().getProperty("open_api_url");
  public final String URL = "http://" + HOST + ":" + PORT;
  public final MockServerClient client = new MockServerClient(HOST, PORT);

}
