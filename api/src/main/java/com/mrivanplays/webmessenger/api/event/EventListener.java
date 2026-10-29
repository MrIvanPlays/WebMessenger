package com.mrivanplays.webmessenger.api.event;

import java.util.function.Consumer;

public interface EventListener<T> {

  static <T> EventListener<T> of(Consumer<T> consumer) {
    return of(EventPriority.NORMAL, consumer);
  }

  static <T> EventListener<T> of(EventPriority priority, Consumer<T> consumer) {
    return new EventListener<T>() {
      @Override
      public EventPriority priority() {
        return priority;
      }

      @Override
      public void handle(T event) {
        consumer.accept(event);
      }
    };
  }

  EventPriority priority();

  void handle(T event);
}
