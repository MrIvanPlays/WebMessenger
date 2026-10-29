package com.mrivanplays.webmessenger.api.event.bundled;

import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;

/**
 * Event, called when a {@link com.mrivanplays.webmessenger.api.request.CustomRequest custom
 * request} is registered to the WebMessenger web server
 */
public class CustomRequestRegisterEvent implements HandleableEvent {

  private Response response;
  private final RequestMetadata requestMetadata;

  public CustomRequestRegisterEvent(RequestMetadata requestMetadata) {
    this.response = Response.unhandled();
    this.requestMetadata = requestMetadata;
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
   * Get the {@link RequestMetadata request metadata} which the request is registered with.
   *
   * @return metadata
   */
  public RequestMetadata requestMetadata() {
    return this.requestMetadata;
  }
}
