package com.mrivanplays.webmessenger.api.event;

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
  Response response();

  /**
   * Mark whether the event action is handled by the {@link EventListener event listener} or not.
   *
   * @param response response
   */
  void handle(Response response);

  interface Response {

    /**
     * Create a new 'handled response'
     *
     * @return new response instance, returning {@link #isHandled()} {@code true}
     */
    static Response handled() {
      return () -> true;
    }

    /**
     * Create a new 'unhandled response'
     *
     * @return new response instance, returning {@link #isHandled()} {@code false}
     */
    static Response unhandled() {
      return () -> false;
    }

    boolean isHandled();
  }
}
