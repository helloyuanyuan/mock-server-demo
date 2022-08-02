package com.solera.global.qa.mockservertest.common;

import io.restassured.http.Header;
import org.mockserver.model.HttpStatusCode;

public enum AuthHeader {
  OK {
    public Header header() {
      return new Header("Authorization", "Bearer " + HttpStatusCode.OK_200);
    }
  },
  UNAUTHORIZED {
    public Header header() {
      return new Header("Authorization", "Bearer " + HttpStatusCode.UNAUTHORIZED_401);
    }
  };

  public abstract Header header();
}
