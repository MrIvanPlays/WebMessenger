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

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a rate limiter interface.
 *
 * <p>A rate limiter in web terms is a way to limit requests to a specific number per
 * second/minute/etc for a known request maker. In our case, that common is the {@link
 * InetSocketAddress address}, from which the request comes from.
 */
@NullMarked
public interface RateLimiter {

  /**
   * Get the settings of this rate limiter.
   *
   * @return settings
   */
  Settings settings();

  /**
   * Process a new request from the specified {@link InetSocketAddress address}
   *
   * @param address address
   * @return shall return true if the address has not hit the criteria for rate limiting, false
   *     otherwise
   */
  boolean process(InetSocketAddress address);

  /**
   * Get the remaining requests of a specified {@link InetSocketAddress address} before that
   * address's {@link #process} returns {@code false}
   *
   * @param address address
   * @return remaining requests
   */
  long remainingRequests(InetSocketAddress address);

  /** Represents settings of a rate limiter. */
  interface Settings {

    static Settings of(Duration windowPeriod, long threshold) {
      Objects.requireNonNull(windowPeriod, "windowPeriod");
      return new Settings() {
        @Override
        public Duration windowPeriod() {
          return windowPeriod;
        }

        @Override
        public long threshold() {
          return threshold;
        }
      };
    }

    /**
     * Get the {@link Duration duration} period for which the {@link #threshold()} needs to be hit
     * before a rate limit occurs
     *
     * @return period duration
     */
    Duration windowPeriod();

    /**
     * Get the threshold needed to be hit before a rate limit occurs.
     *
     * @return threshold
     */
    long threshold();
  }
}
