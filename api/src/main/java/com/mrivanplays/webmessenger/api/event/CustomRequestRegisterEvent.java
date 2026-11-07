package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.events.ResultedEvent;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Event, called when a {@link com.mrivanplays.webmessenger.api.request.CustomRequest custom
 * request} is registered to the WebMessenger web server
 */
public class CustomRequestRegisterEvent implements ResultedEvent<ResultedEvent.GenericResult> {

  private GenericResult result;
  private final RequestMetadata requestMetadata;

  public CustomRequestRegisterEvent(RequestMetadata requestMetadata) {
    this.result = GenericResult.ALLOWED;
    this.requestMetadata = requestMetadata;
  }

  @Override
  public @NotNull GenericResult result() {
    return result;
  }

  @Override
  public void setResult(@NotNull ResultedEvent.GenericResult newResult) {
    this.result = Objects.requireNonNull(newResult, "newResult");
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
