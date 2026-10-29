package com.mrivanplays.webmessenger.api;

import com.mrivanplays.webmessenger.api.ratelimit.RateLimiter;
import java.util.Optional;

public abstract class WebMessengerServer {

  //
  private static WebMessengerServer instance;

  public static WebMessengerServer getInstance() {
    return instance;
  }

  public static void setInstance(WebMessengerServer newInstance) {
    if (instance != null) {
      return;
    }
    instance = newInstance;
  }

  public abstract Optional<RateLimiter> rateLimiter();

  public abstract void setRateLimiter(RateLimiter rateLimiter);

  public abstract String version();

  public abstract Platform platform();
}
