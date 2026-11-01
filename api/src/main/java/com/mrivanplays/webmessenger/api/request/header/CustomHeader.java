package com.mrivanplays.webmessenger.api.request.header;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  @NotNull
  static CustomHeader ofSingleValue(@NotNull String key, @Nullable String value) {
    return of(key, value == null ? Collections.emptyList() : List.of(value));
  }

  /**
   * Create a new custom header with a {@link String} array of values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
  @NotNull
  static CustomHeader of(@NotNull String key, @Nullable String[] values) {
    return of(key, values == null ? Collections.emptyList() : Arrays.asList(values));
  }

  /**
   * Create a new custom header with a {@link List} of {@link String} values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
  @NotNull
  static CustomHeader of(@NotNull String key, @Nullable List<String> values) {
    Objects.requireNonNull(key, "key");
    return new CustomHeader() {
      @Override
      @NotNull
      public String key() {
        return key;
      }

      @Override
      @Nullable
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
  @NotNull
  String key();

  /**
   * Get the values of the custom header.
   *
   * @return values
   */
  @Nullable
  List<String> value();
}
