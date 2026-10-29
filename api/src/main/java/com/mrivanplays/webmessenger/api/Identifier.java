package com.mrivanplays.webmessenger.api;

public record Identifier(String key, String value) {

  public String asName() {
    if (key == null || value == null) {
      throw new IllegalArgumentException("Illegal identifier provided");
    }
    return key + ":" + value;
  }
}
