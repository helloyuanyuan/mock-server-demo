package com.solera.global.qa.mockservertest.beans;

import lombok.Data;

@Data
public class SkillResult {

  private Integer id;
  private String name;

  public SkillResult() {}

  public SkillResult(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
}
