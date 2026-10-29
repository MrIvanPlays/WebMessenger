package com.mrivanplays.webmessenger.velocity;

import com.mrivanplays.webmessenger.core.log.PluginLogger;
import org.slf4j.Logger;

public class VelocityPluginLogger implements PluginLogger {

  private final Logger parent;
  private boolean enableDebugMessages = false;

  public VelocityPluginLogger(Logger parent) {
    this.parent = parent;
  }

  @Override
  public void info(String message) {
    this.parent.info(message);
  }

  @Override
  public void warn(String message) {
    this.parent.warn(message);
  }

  @Override
  public void error(String message) {
    this.parent.error(message);
  }

  @Override
  public void error(String message, Exception e) {
    this.parent.error(message, e);
  }

  @Override
  public void debug(String message) {
    if (enableDebugMessages) {
      parent.debug(message);
    }
  }

  @Override
  public void enableDebugMessages() {
    parent.debug("Enabled debug messages");
    this.enableDebugMessages = true;
  }
}
