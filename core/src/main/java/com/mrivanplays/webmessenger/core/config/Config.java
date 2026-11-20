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
package com.mrivanplays.webmessenger.core.config;

import com.mrivanplays.annotationconfig.core.annotations.Ignore;
import com.mrivanplays.annotationconfig.core.annotations.Key;
import com.mrivanplays.annotationconfig.core.annotations.comment.Comment;
import java.time.Duration;

@Comment("Configuration file of WebMessenger")
public class Config {

  @Comment("Port to listen to")
  private int port = 8082;

  @Comment("A secret key which all requests should contain")
  @Comment("in the request header. If a request does not")
  @Comment("contain the secret key, the web server will")
  @Comment("respond with 403 (or 'forbidden')")
  private String secret = "43ab005peek2ehrje";

  @Comment("Shall a rate limiter be used, how many requests")
  @Comment("need to pass in the given time before a rate limit occurs")
  @Comment("Default settings are 3 requests in 5 seconds, more will result")
  @Comment("in a rate limit")
  @Key("enable-rate-limit")
  private boolean enableRateLimit = false;

  @Key("rate-limit")
  private int rateLimit = -1;

  @Comment("Minimum value of 1 second")
  @Key("rate-limit-time")
  private int rateLimitTime = 5;

  @Comment("Enable debug logging messages")
  @Comment("Accepted values: true/false")
  @Key("enable-debug")
  private boolean enableDebug = false;

  public int getPort() {
    return port;
  }

  public String getSecret() {
    return secret;
  }

  public boolean isRateLimitEnabled() {
    return this.enableRateLimit;
  }

  public int getRateLimit() {
    return rateLimit;
  }

  @Ignore private Duration rateLimitDuration;

  public Duration getRateLimitDuration() {
    if (this.rateLimitDuration == null) {
      this.rateLimitDuration = Duration.ofSeconds(rateLimitTime);
    }
    return this.rateLimitDuration;
  }

  public boolean isDebugEnabled() {
    return this.enableDebug;
  }
}
