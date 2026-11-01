package com.mrivanplays.webmessenger.api;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a platform independent Minecraft {@code NamespacedKey} like identifier.
 *
 * @param key key identifier (non-null)
 * @param value value (or slave) identifier of the key (non-null)
 */
public record Identifier(@NotNull String key, @NotNull String value) {

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
  @NotNull
  public String asName() {
    return key + ":" + value;
  }
}
