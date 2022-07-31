package com.solera.global.qa.mockservertest;

import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.solera.global.qa.mockservertest.common.PropertyUtils;

public class MockServerTestBase {

  protected final String HOST = PropertyUtils.getInstance().getProperty("host");
  protected final int PORT = Integer.parseInt(PropertyUtils.getInstance().getProperty("port"));
  protected final String OPENAPIURL = PropertyUtils.getInstance().getProperty("openapiurl");
  protected final String MOCKSERVERURL = PropertyUtils.getInstance().getUrl(HOST, PORT);
  protected final MockServerClient client = new MockServerClient(HOST, PORT);

  @Autowired
  @Lazy
  protected MockServerUtils mockServerUtils;

}
