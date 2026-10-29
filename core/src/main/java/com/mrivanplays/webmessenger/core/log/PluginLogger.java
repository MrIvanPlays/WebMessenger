package com.mrivanplays.webmessenger.core.log;

import java.util.logging.Logger;

public interface PluginLogger {

  static PluginLogger fromJavaLogger(Logger logger) {
    return new JavaPluginLogger(logger);
  }

  void info(String message);

  void warn(String message);

  void error(String message);

  void error(String message, Exception e);

  void debug(String message);

  void enableDebugMessages();
}
