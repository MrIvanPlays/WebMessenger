package com.mrivanplays.webmessenger.api.event.bundled;

import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;

public class HttpRequestHandleEvent implements HandleableEvent {
  private RequestResponseContext ctx;
  private Response response;

  public HttpRequestHandleEvent(RequestResponseContext ctx) {
    this.ctx = ctx;
    this.response = Response.unhandled();
  }

  @Override
  public Response response() {
    return this.response;
  }

  @Override
  public void handle(Response response) {
    this.response = response;
  }

  public RequestResponseContext context() {
    return ctx;
  }
}
