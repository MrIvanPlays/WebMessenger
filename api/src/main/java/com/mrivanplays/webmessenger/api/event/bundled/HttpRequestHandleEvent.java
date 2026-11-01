package com.mrivanplays.webmessenger.api.event.bundled;

import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/** Event, called when a HTTP request is to be handled by the WebMessenger web server */
public class HttpRequestHandleEvent implements HandleableEvent {
  private final RequestResponseContext ctx;
  private Response response;

  public HttpRequestHandleEvent(@NotNull RequestResponseContext ctx) {
    this.ctx = Objects.requireNonNull(ctx, "context");
    this.response = Response.unhandled();
  }

  /** {@inheritDoc} */
  @NotNull
  @Override
  public Response response() {
    return this.response;
  }

  /** {@inheritDoc} */
  @Override
  public void handle(@NotNull Response response) {
    Objects.requireNonNull(response, "response");
    this.response = response;
  }

  /**
   * Get the {@link RequestResponseContext response context} for this request.
   *
   * @return context
   */
  @NotNull
  public RequestResponseContext context() {
    return ctx;
  }
}
