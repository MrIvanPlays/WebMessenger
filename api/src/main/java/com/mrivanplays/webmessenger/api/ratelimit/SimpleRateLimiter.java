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
package com.mrivanplays.webmessenger.api.ratelimit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.jspecify.annotations.NullMarked;

/** Represents a simple window based rate limiter */
@NullMarked
public final class SimpleRateLimiter implements RateLimiter {

  /**
   * Create a new rate limiter
   *
   * @param settings rate limit settings
   * @return new simple rate limiter
   */
  public static SimpleRateLimiter of(Settings settings) {
    return new SimpleRateLimiter(settings);
  }

  private final Settings settings;
  private final Map<InetAddress, Deque<Long>> stored = new ConcurrentHashMap<>();

  private SimpleRateLimiter(Settings settings) {
    this.settings = Objects.requireNonNull(settings, "settings");
  }

  /** {@inheritDoc} */
  @Override
  public Settings settings() {
    return settings;
  }

  /** {@inheritDoc} */
  @Override
  public boolean process(InetSocketAddress address) {
    Objects.requireNonNull(address, "address");
    Deque<Long> timestamps = stored.get(address.getAddress());
    long currentTimeMillis = System.currentTimeMillis();
    if (timestamps == null) {
      timestamps = new ConcurrentLinkedDeque<>();
      timestamps.addLast(System.currentTimeMillis());
      stored.put(address.getAddress(), timestamps);
      return true;
    }

    // cleanup
    while (!timestamps.isEmpty()
        && timestamps.getFirst()
            < (currentTimeMillis - (settings.windowPeriod().getSeconds() * 1000))) {
      timestamps.removeFirst();
    }

    if (timestamps.size() >= settings.threshold()) {
      return false;
    }

    timestamps.addLast(currentTimeMillis);
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public long remainingRequests(InetSocketAddress address) {
    Objects.requireNonNull(address, "address");
    Deque<Long> timestamps = stored.get(address.getAddress());
    if (timestamps == null) {
      return settings.threshold();
    }
    return settings.threshold() - timestamps.size();
  }
}
