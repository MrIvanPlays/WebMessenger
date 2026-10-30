package com.mrivanplays.webmessenger.api.ratelimit;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Represents a rate limiter interface.
 *
 * <p>A rate limiter in web terms is a way to limit requests to a specific number per
 * second/minute/etc for a known request maker. In our case, that common is the {@link
 * InetSocketAddress address}, from which the request comes from.
 */
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
