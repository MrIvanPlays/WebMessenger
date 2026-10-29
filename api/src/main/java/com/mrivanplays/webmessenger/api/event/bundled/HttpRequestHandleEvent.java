package com.mrivanplays.webmessenger.api.event.bundled;

import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;

/** Event, called when a HTTP request is to be handled by the WebMessenger web server */
public class HttpRequestHandleEvent implements HandleableEvent {
  private RequestResponseContext ctx;
  private Response response;

  public HttpRequestHandleEvent(RequestResponseContext ctx) {
    this.ctx = ctx;
    this.response = Response.unhandled();
  }

  /** {@inheritDoc} */
  @Override
  public Response response() {
    return this.response;
  }

  /** {@inheritDoc} */
  @Override
  public void handle(Response response) {
    this.response = response;
  }

  /**
   * Get the {@link RequestResponseContext response context} for this request.
   *
   * @return context
   */
  public RequestResponseContext context() {
    return ctx;
  }
}
