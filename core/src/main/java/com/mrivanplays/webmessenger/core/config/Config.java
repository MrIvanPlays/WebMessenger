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

  private int port;
  private final String secret;
  private final boolean enableRateLimit;
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
    }
    Yaml yaml = new Yaml();
    try {
      Map<String, Object> loaded = yaml.load(new FileInputStream(file));
      try {
        this.port = Integer.parseInt(String.valueOf(loaded.get("port")));
      } catch (NumberFormatException e) {
        logger.warn("Invalid port provided, falling back to port 8082");
        this.port = 8082;
      }
      this.secret = String.valueOf(loaded.get("secret"));
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
          this.rateLimitDuration = Duration.ofSeconds(5);
        }
      }
      boolean debugMessages = Boolean.parseBoolean(String.valueOf(loaded.get("enable-debug")));
      if (debugMessages) {
        logger.enableDebugMessages();
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Could not find configuration file", e);
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
