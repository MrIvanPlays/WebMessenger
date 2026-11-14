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

import com.mrivanplays.webmessenger.core.log.PluginLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Config {

  private int port = 8082;
  private String secret = "43ab005peek2ehrje";
  private boolean enableRateLimit = false;
  private int rateLimit = -1;
  private Duration rateLimitDuration = Duration.ofSeconds(5);

  public Config(File file, PluginLogger logger) {
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdir();
      }
      try {
        Files.copy(getClass().getClassLoader().getResourceAsStream("config.yml"), file.toPath());
      } catch (IOException e) {
        logger.error("Exception during config.yml creation", e);
      }
      return;
    }
    Yaml yaml = new Yaml();
    try {
      Map<String, Object> loaded = yaml.load(new FileInputStream(file));
      try {
        this.port = Integer.parseInt(String.valueOf(loaded.get("port")));
      } catch (NumberFormatException e) {
        logger.warn("Invalid port provided, falling back to port 8082");
      }
      var defSecret = this.secret;
      this.secret = String.valueOf(loaded.get("secret"));
      if (defSecret.equals(this.secret)) {
        logger.warn("Please change the default secret in config.yml with your own");
      }
      this.enableRateLimit = Boolean.parseBoolean(String.valueOf(loaded.get("enable-rate-limit")));
      if (enableRateLimit) {
        logger.info("Rate limiter enabled");
        try {
          this.rateLimit = Integer.parseInt(String.valueOf(loaded.get("rate-limit")));
        } catch (NumberFormatException e) {
          logger.warn("Invalid rate limit request number provided, falling back to 3 (requests)");
          this.rateLimit = 3;
        }
        try {
          this.rateLimitDuration =
              Duration.ofSeconds(Long.parseLong(String.valueOf(loaded.get("rate-limit-time"))));
        } catch (NumberFormatException e) {
          logger.warn("Invalid rate limit period window provided, falling back to 5 (seconds)");
        }
      }
      boolean debugMessages = Boolean.parseBoolean(String.valueOf(loaded.get("enable-debug")));
      if (debugMessages) {
        logger.enableDebugMessages();
      }
    } catch (FileNotFoundException e) {
      logger.error("Could not load config.yml", e);
    }
  }

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

  public Duration getRateLimitDuration() {
    return rateLimitDuration;
  }
}
