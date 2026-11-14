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
package com.mrivanplays.webmessenger.api;

import com.mrivanplays.events.ListenerIdentifier;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a platform independent Minecraft {@code NamespacedKey} like identifier.
 *
 * @param key key identifier (non-null)
 * @param value value (or slave) identifier of the key (non-null)
 */
@NullMarked
public record Identifier(String key, String value) implements ListenerIdentifier {

  public Identifier {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
    if (key.isEmpty()) {
      throw new IllegalArgumentException("key cannot be empty");
    }
    if (value.isEmpty()) {
      throw new IllegalArgumentException("value cannot be empty");
    }
  }

  /**
   * Return this identifier as a Minecraft-like {@code NamespacedKey} String
   *
   * @return 'key:value'
   */
  public String asName() {
    return key + ":" + value;
  }

  /** {@inheritDoc} */
  @Override
  public boolean matches(ListenerIdentifier other) {
    if (!(other instanceof Identifier identified)) return false;
    if (identified == this) return true;
    return identified.key().equals(key) && identified.value().equals(value);
  }
}
