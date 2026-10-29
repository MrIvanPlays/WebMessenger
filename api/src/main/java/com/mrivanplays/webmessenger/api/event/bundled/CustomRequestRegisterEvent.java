package com.mrivanplays.webmessenger.api.event.bundled;

import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;

public class CustomRequestRegisterEvent implements HandleableEvent {

  private Response response;
  private final RequestMetadata requestMetadata;

  public CustomRequestRegisterEvent(RequestMetadata requestMetadata) {
    this.response = Response.unhandled();
    this.requestMetadata = requestMetadata;
  }

  @Override
  public Response response() {
    return this.response;
  }

  @Override
  public void handle(Response response) {
    this.response = response;
  }

  public RequestMetadata requestMetadata() {
    return this.requestMetadata;
  }
}
