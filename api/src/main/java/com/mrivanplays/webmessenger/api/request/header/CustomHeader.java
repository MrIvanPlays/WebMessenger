/*
 * Copyright (c) Ivan Pekov 2026
 * Copyright (c) Contributors 2026
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mrivanplays.webmessenger.api.request.header;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents an HTTP Header to be used in {@link
 * com.mrivanplays.webmessenger.api.request.CustomRequest custom requests}
 */
@NullMarked
public interface CustomHeader {

  /**
   * Create a new custom header with a single value
   *
   * @param key header key
   * @param value header value
   * @return new custom header instance
   */
  static CustomHeader ofSingleValue(String key, @Nullable String value) {
    return of(key, value == null ? Collections.emptyList() : List.of(value));
  }

  /**
   * Create a new custom header with a {@link String} array of values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
  static CustomHeader of(String key, String @Nullable [] values) {
    return of(key, values == null ? Collections.emptyList() : Arrays.asList(values));
  }

  /**
   * Create a new custom header with a {@link List} of {@link String} values
   *
   * @param key header key
   * @param values header values
   * @return new custom header instance
   */
  static CustomHeader of(String key, @Nullable List<String> values) {
    Objects.requireNonNull(key, "key");
    return new CustomHeader() {
      @Override
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
  String key();

  /**
   * Get the values of the custom header.
   *
   * @return values
   */
  @Nullable List<String> value();
}
