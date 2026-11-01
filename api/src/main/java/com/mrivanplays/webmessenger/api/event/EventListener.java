package com.mrivanplays.webmessenger.api.event;

import java.util.Objects;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a listener of an {@link Event}
 *
 * @param <T> event to listen to
 */
public interface EventListener<T extends Event> {

  /**
   * Creates a new event listener with {@link EventPriority#NORMAL normal priority}
   *
   * @param consumer handle the passed event function
   * @return new event listener instance
   * @param <T> event type
   */
  @NotNull
  static <T extends Event> EventListener<T> of(@NotNull Consumer<T> consumer) {
    return of(EventPriority.NORMAL, consumer);
  }

  /**
   * Creates a new event listener
   *
   * @param priority priority with which this event listener is to be sorted
   * @param consumer handle the passed event function
   * @return new event listener instance
   * @param <T> event type
   */
  @NotNull
  static <T extends Event> EventListener<T> of(
      @NotNull EventPriority priority, @NotNull Consumer<T> consumer) {
    Objects.requireNonNull(priority, "priority");
    Objects.requireNonNull(consumer, "consumer");
    return new EventListener<>() {
      @Override
      @NotNull
      public EventPriority priority() {
        return priority;
      }

      @Override
      public void handle(@NotNull T event) {
        consumer.accept(event);
      }
    };
  }

  /**
   * Get the {@link EventPriority priority} at which this event listener is to be called.
   *
   * @return priority
   */
  @NotNull
  EventPriority priority();

  /**
   * Handle the passed event. Called hwne {@link EventBus#fire} is called with the event type this
   * listener is registered for.
   *
   * @param event event to handle
   */
  void handle(@NotNull T event);
}
