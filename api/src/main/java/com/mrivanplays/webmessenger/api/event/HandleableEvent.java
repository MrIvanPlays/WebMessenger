package com.mrivanplays.webmessenger.api.event;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a handleable resulted event. When such event is handled (or {@link
 * Response#isHandled()} returns {@code true}), WebMessenger web server will not continue with
 * handling the event action for which a handled event is called
 */
public interface HandleableEvent extends Event {

  /**
   * Get the response of the event
   *
   * @return response
   */
  @NotNull
  Response response();

  /**
   * Mark whether the event action is handled by the {@link EventListener event listener} or not.
   *
   * @param response response
   */
  void handle(@NotNull Response response);

  interface Response {

    /**
     * Create a new 'handled response'
     *
     * @return new response instance, returning {@link #isHandled()} {@code true}
     */
    static @NotNull Response handled() {
      return () -> true;
    }

    /**
     * Create a new 'unhandled response'
     *
     * @return new response instance, returning {@link #isHandled()} {@code false}
     */
    static @NotNull Response unhandled() {
      return () -> false;
    }

    boolean isHandled();
  }
}
