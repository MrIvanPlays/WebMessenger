package com.mrivanplays.webmessenger.api;

import com.mrivanplays.webmessenger.api.ratelimit.RateLimiter;
import java.util.Optional;

/**
 * Represents a singleton class of accessing the WebMessenger web server in a plugin controlled
 * manner
 */
public abstract class WebMessengerServer {

  //
  private static WebMessengerServer instance;

  /**
   * Get the implementation instance
   *
   * @return depending on when accessed, may be null
   */
  public static WebMessengerServer getInstance() {
    return instance;
  }

  public static void setInstance(WebMessengerServer newInstance) {
    if (instance != null) {
      return;
    }
    instance = newInstance;
  }

  //

  /**
   * Get the rate limiter used on the web server
   *
   * @return rate limiter
   */
  public abstract Optional<RateLimiter> rateLimiter();

  /**
   * Set a new rate limiter to be used for default route handlers which are bundled in the
   * WebMessenger plugin
   *
   * @param rateLimiter new rate limiter
   */
  public abstract void setRateLimiter(RateLimiter rateLimiter);

  /**
   * Get the WebMessenger plugin version
   *
   * @return plugin version
   */
  public abstract String version();

  /**
   * Get the Minecraft server {@link Platform} type on which the WebMessenger web server is running
   * on.
   *
   * @return platform
   */
  public abstract Platform platform();
}
