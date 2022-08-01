package com.solera.global.qa.mockservertest.beans;

import lombok.Data;

@Data
public class Error {

  private Integer code;
  private String message;

  public Error() {}

  public Error(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
