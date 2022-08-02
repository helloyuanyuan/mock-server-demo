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
  },
  NOT_FOUND {
    public Header header() {
      return new Header("Authorization", "Bearer " + HttpStatusCode.NOT_FOUND_404);
    }
  },
  INTERNAL_SERVER_ERROR {
    public Header header() {
      return new Header("Authorization", "Bearer " + HttpStatusCode.INTERNAL_SERVER_ERROR_500);
    }
  };

  public abstract Header header();
}
