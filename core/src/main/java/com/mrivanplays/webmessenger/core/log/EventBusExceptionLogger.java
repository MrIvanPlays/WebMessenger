package com.mrivanplays.webmessenger.core.log;

import com.mrivanplays.events.ExceptionLogger;

public class EventBusExceptionLogger implements ExceptionLogger {

  private final PluginLogger parent;

  public EventBusExceptionLogger(PluginLogger parent) {
    this.parent = parent;
  }

  @Override
  public void log(Throwable e) {
    parent.error("Exception during event handling", e);
  }
}
