package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.apiimpl.RequestResponseContextImpl;
import com.mrivanplays.webmessenger.core.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

public class CustomRequestRegistration extends MessengerHttpHandler {
  private final CustomRequest request;

  public CustomRequestRegistration(WebMessengerPlugin plugin, CustomRequest request) {
    super(plugin);
    this.request = request;
  }

  @Override
  public RequestMetadata metadata() {
    return request.metadata();
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    List<CustomHeader> requiredHeaders = request.metadata().requiredHeaders();
    if (!requiredHeaders.isEmpty()) {
      boolean shallHandle = true;
      for (CustomHeader header : requiredHeaders) {
        List<String> values = exchange.getRequestHeaders().getOrDefault(header.key(), null);
        if (values == null) {
          shallHandle = false;
          break;
        }
        if (!Utils.listEquals(values, header.value())) {
          shallHandle = false;
          break;
        }
      }
      if (!shallHandle) {
        this.handleGenericErrorResponse(exchange, "Invalid or missing request headers", 400);
        return;
      }
    }

    request.handle(new RequestResponseContextImpl(exchange, gson, request.metadata()));
  }
}
