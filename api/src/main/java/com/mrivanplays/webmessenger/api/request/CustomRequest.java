package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.utils.ConsumerThrowable;
import java.io.IOException;

public interface CustomRequest {

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

  RequestMetadata metadata();

  void handle(RequestResponseContext ctx) throws IOException;
}
