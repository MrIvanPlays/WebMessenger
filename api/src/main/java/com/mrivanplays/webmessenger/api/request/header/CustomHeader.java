package com.mrivanplays.webmessenger.api.request.header;

import java.util.List;

/**
 * Represents an HTTP Header to be used in {@link
 * com.mrivanplays.webmessenger.api.request.CustomRequest custom requests}
 */
public interface CustomHeader {

  /**
   * Create a new custom header with a single value
   *
   * @param key header key
   * @param value header value
   * @return new custom header instance
   */
  static CustomHeader ofSingleValue(String key, String value) {
    return of(key, List.of(value));
  }

  /**
   * Create a new custom header with a {@link String} array of values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
  static CustomHeader of(String key, String[] values) {
    return of(key, List.of(values));
  }

  /**
   * Create a new custom header with a {@link List} of {@link String} values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
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

  /**
   * Get the key of the custom header.
   *
   * @return key
   */
  String key();

  /**
   * Get the values of the custom header.
   *
   * @return values
   */
  List<String> value();
}
