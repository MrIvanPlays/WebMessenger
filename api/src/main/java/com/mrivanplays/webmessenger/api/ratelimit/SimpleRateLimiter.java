package com.mrivanplays.webmessenger.api.ratelimit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.jetbrains.annotations.NotNull;

/** Represents a simple window based rate limiter */
public final class SimpleRateLimiter implements RateLimiter {

  /**
   * Create a new rate limiter
   *
   * @param settings rate limit settings
   * @return new simple rate limiter
   */
  @NotNull
  public static SimpleRateLimiter of(@NotNull Settings settings) {
    return new SimpleRateLimiter(settings);
  }

  private final Settings settings;
  private final Map<InetAddress, Deque<Long>> stored = new ConcurrentHashMap<>();

  private SimpleRateLimiter(@NotNull Settings settings) {
    this.settings = Objects.requireNonNull(settings, "settings");
  }

  /** {@inheritDoc} */
  @Override
  @NotNull
  public Settings settings() {
    return settings;
  }

  /** {@inheritDoc} */
  @Override
  public boolean process(@NotNull InetSocketAddress address) {
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
  public long remainingRequests(@NotNull InetSocketAddress address) {
    Objects.requireNonNull(address, "address");
    Deque<Long> timestamps = stored.get(address.getAddress());
    if (timestamps == null) {
      return settings.threshold();
    }
    return settings.threshold() - timestamps.size();
  }
}
