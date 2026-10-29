package com.mrivanplays.webmessenger.core.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaPluginLogger implements PluginLogger {

  private Logger parent;
  private boolean enableDebugMessages = false;

  public JavaPluginLogger(Logger parent) {
    this.parent = parent;
  }

  @Override
  public void info(String message) {
    parent.info(message);
  }

  @Override
  public void warn(String message) {
    parent.warning(message);
  }

  @Override
  public void error(String message) {
    parent.severe(message);
  }

  @Override
  public void error(String message, Exception e) {
    parent.log(Level.SEVERE, message, e);
  }

  @Override
  public void debug(String message) {
    if (enableDebugMessages) {
      info("[DEBUG] " + message);
    }
  }

  @Override
  public void enableDebugMessages() {
    info("[DEBUG] Debug messages enabled");
    this.enableDebugMessages = true;
  }
}
