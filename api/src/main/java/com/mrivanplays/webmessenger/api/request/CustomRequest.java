package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.utils.ConsumerThrowable;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/** Represents a custom request that the WebMessenger web server is going to handle. */
public interface CustomRequest {

  /**
   * Create a custom request without implementing the interface
   *
   * @param metadata request metadata
   * @param consumer custom request handler
   * @return new {@code CustomRequest} instance
   */
  @NotNull
  static CustomRequest functional(
      @NotNull RequestMetadata metadata,
      @NotNull ConsumerThrowable<RequestResponseContext, IOException> consumer) {
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(consumer, "consumer");
    return new CustomRequest() {
      @Override
      @NotNull
      public RequestMetadata metadata() {
        return metadata;
      }

      @Override
      public void handle(@NotNull RequestResponseContext ctx) throws IOException {
        consumer.accept(ctx);
      }
    };
  }

  /**
   * Get the {@link RequestMetadata metadata} of this request
   *
   * @return metadata
   */
  @NotNull
  RequestMetadata metadata();

  /**
   * Handle the custom request. WARNING: This method can only be called via the WebMessenger web
   * server, any calls from your hook will result in a error.
   *
   * @param ctx response context
   * @throws IOException if an I/O error occurs
   */
  @ApiStatus.OverrideOnly
  void handle(@NotNull RequestResponseContext ctx) throws IOException;
}
