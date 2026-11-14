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

import com.mrivanplays.events.EventBus;
import com.mrivanplays.webmessenger.api.ratelimit.RateLimiter;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a singleton class of accessing the WebMessenger web server in a plugin controlled
 * manner
 */
@NullMarked
public abstract class WebMessengerServer {

  //
  private static @Nullable WebMessengerServer instance;

  /**
   * Get the implementation instance
   *
   * @return depending on when accessed, may be null
   */
  @Nullable
  public static WebMessengerServer getInstance() {
    return instance;
  }

  public static void setInstance(WebMessengerServer newInstance) {
    if (instance != null) {
      throw new UnsupportedOperationException("WebMessengerServer has already been initialized.");
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
  public abstract void setRateLimiter(@Nullable RateLimiter rateLimiter);

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

  /**
   * Get the registrar where to register custom requests
   *
   * @return custom request registrar
   */
  public abstract CustomRequestRegistrar customRequestRegistrar();

  /**
   * Get the event bus of this server for listening to the packaged events with the WebMessenger API
   *
   * @return event bus
   */
  public abstract EventBus eventBus();
}
