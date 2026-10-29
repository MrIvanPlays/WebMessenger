package com.mrivanplays.webmessenger.api.ratelimit;

import java.net.InetSocketAddress;
import java.time.Duration;

public interface RateLimiter {

  Settings settings();

  boolean process(InetSocketAddress address);

  long remainingRequests(InetSocketAddress address);

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

    Duration windowPeriod();

    long threshold();
  }
}
