package com.mrivanplays.webmessenger.api.request.header;

import java.util.List;

public interface CustomHeader {

  static CustomHeader ofSingleValue(String key, String value) {
    return of(key, List.of(value));
  }

  static CustomHeader of(String key, String[] values) {
    return of(key, List.of(values));
  }

  static CustomHeader of(String key, List<String> values) {
    return new CustomHeader() {
      @Override
      public String key() {
        return key;
      }

      @Override
      public List<String> value() {
        return values;
      }
    };
  }

  String key();

  List<String> value();
}
