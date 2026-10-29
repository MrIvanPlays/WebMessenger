package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.utils.ConsumerThrowable;
import java.io.IOException;

/** Represents a custom request that the WebMessenger web server is going to handle. */
public interface CustomRequest {

  /**
   * Create a custom request without implementing the interface
   *
   * @param metadata request metadata
   * @param consumer custom request handler
   * @return new {@code CustomRequest} instance
   */
  static CustomRequest functional(
      RequestMetadata metadata, ConsumerThrowable<RequestResponseContext, IOException> consumer) {
    return new CustomRequest() {
      @Override
      public RequestMetadata metadata() {
        return metadata;
      }

      @Override
      public void handle(RequestResponseContext ctx) throws IOException {
        consumer.accept(ctx);
      }
    };
  }

  /**
   * Get the {@link RequestMetadata metadata} of this request
   *
   * @return metadata
   */
  RequestMetadata metadata();

  /**
   * Handle the custom request. WARNING: This method can only be called via the WebMessenger web
   * server, any calls from your hook will result in a error.
   *
   * @param ctx response context
   * @throws IOException if an I/O error occurs
   */
  void handle(RequestResponseContext ctx) throws IOException;
}
