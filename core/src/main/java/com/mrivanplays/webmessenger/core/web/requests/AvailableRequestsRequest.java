package com.mrivanplays.webmessenger.core.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestType;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableRequestsRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;
  private final List<Map.Entry<String, RequestType>> defaults = new ArrayList<>();

  public AvailableRequestsRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/availableRequests");
  }

  public void populateDefaults(Map<String, MessengerHttpHandler> handlers) {
    for (Map.Entry<String, MessengerHttpHandler> entry : handlers.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("/")) {
        continue;
      }
      defaults.add(
          new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().metadata().type()));
    }
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    JsonArray array = new JsonArray();
    for (Map.Entry<String, RequestType> entry : defaults) {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", entry.getValue().name());
      obj.addProperty("route", entry.getKey());
      array.add(obj);
    }
    for (CustomRequest custom : CustomRequestRegistrar.INSTANCE.getRegistrar()) {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", custom.metadata().type().name());
      obj.addProperty("route", custom.metadata().route());
      array.add(obj);
    }
    this.respond(exchange, array, 200);
  }
}
