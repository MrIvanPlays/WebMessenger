package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestType;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class NotFoundRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public NotFoundRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata = RequestMetadata.of(WebMessengerPlugin.API_IDENTIFIER, "/", RequestType.ALL);
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    this.handleGenericErrorResponse(exchange, "Not Found", 404);
  }

  @Override
  protected boolean shouldAuthenticate() {
    return false;
  }
}
