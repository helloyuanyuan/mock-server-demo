package com.example.mockserverdemo.beans;

import lombok.Data;

@Data
public class Pet {

  private Integer id;
  private String name;
  private String tag;

  public Pet() {}

  public Pet(Integer id, String name, String tag) {
    this.id = id;
    this.name = name;
    this.tag = tag;
  }

}
