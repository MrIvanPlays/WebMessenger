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

import org.jspecify.annotations.NonNull;

/**
 * Represents the platform the web messenger server is running on
 *
 * @author Ivan Pekov
 * @since 0.0.1
 */
public enum Platform {
  BUNGEECORD("BungeeCord"),
  BUKKIT_LIKE("Bukkit like"),
  /*
   * We know paper is a BUKKIT_LIKE platform, however the bukkit plugin
   * implementation once uses a API call that only exists on paper (and its forks),
   * hence the redundancy reason
   */
  PAPER_LIKE("Paper like"),
  VELOCITY("Velocity");

  private final String prettyName;

  Platform(String prettyName) {
    this.prettyName = prettyName;
  }

  /**
   * Check whether the given platform is bukkit like
   *
   * @return whether bukkit
   */
  public boolean isBukkitLike() {
    return this == BUKKIT_LIKE || this == PAPER_LIKE;
  }

  /**
   * Check whether the given platform is a proxy
   *
   * @return whether proxy
   */
  public boolean isProxy() {
    return this == BUNGEECORD || this == VELOCITY;
  }

  /**
   * Get a pretty {@link java.lang.String} {@code name} of the platform
   *
   * @return pretty name
   */
  @NonNull
  public String prettyName() {
    return this.prettyName;
  }
}
