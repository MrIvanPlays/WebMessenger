package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.events.EventBus;

class EBusInstance {

  private static EventBus instance;

  static EventBus get() {
    return instance;
  }

  static void set(EventBus ins) {
    if (instance == null) {
      instance = ins;
    }
  }
}
