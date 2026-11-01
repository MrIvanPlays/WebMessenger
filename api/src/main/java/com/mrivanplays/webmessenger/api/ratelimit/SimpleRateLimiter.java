package com.mrivanplays.webmessenger.api.ratelimit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

// todo: javadoc
public final class SimpleRateLimiter implements RateLimiter {

  @NotNull
  public static SimpleRateLimiter of(@NotNull Settings settings) {
    return new SimpleRateLimiter(settings);
  }

  private final Settings settings;
  private final Map<InetAddress, Deque<Long>> stored = new HashMap<>();

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
      timestamps = new ArrayDeque<>();
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
