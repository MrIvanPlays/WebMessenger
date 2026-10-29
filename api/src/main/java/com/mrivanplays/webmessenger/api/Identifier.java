package com.mrivanplays.webmessenger.api;

import java.util.Objects;

/**
 * Represents a platform independent Minecraft {@code NamespacedKey} like identifier.
 *
 * @param key key identifier (non-null)
 * @param value value (or slave) identifier of the key (non-null)
 */
public record Identifier(String key, String value) {

  public Identifier {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
  }

  /**
   * Return this identifier as a Minecraft-like {@code NamespacedKey} String
   *
   * @return 'key:value'
   */
  public String asName() {
    return key + ":" + value;
  }
}
