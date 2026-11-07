package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.events.ResultedEvent;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/** Event, called when a HTTP request is to be handled by the WebMessenger web server */
public class HttpRequestHandleEvent implements ResultedEvent<ResultedEvent.GenericResult> {
  private final RequestResponseContext ctx;
  private GenericResult result;

  public HttpRequestHandleEvent(@NotNull RequestResponseContext ctx) {
    this.ctx = Objects.requireNonNull(ctx, "context");
    this.result = GenericResult.ALLOWED;
  }

  /** {@inheritDoc} */
  @Override
  public @NotNull GenericResult result() {
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public void setResult(@NotNull ResultedEvent.GenericResult newResult) {
    this.result = Objects.requireNonNull(newResult, "newResult");
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
