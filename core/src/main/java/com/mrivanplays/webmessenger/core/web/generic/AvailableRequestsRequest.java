package com.mrivanplays.webmessenger.core.web.generic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestType;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.apiimpl.CustomRequestRegistrarImpl;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AvailableRequestsRequest extends MessengerHttpHandler {

  record DescribedMetadata(RequestType requestType, String description) {}

  private final RequestMetadata metadata;
  private final Set<Map.Entry<String, DescribedMetadata>> struct =
      Collections.newSetFromMap(new ConcurrentHashMap<>());

  public AvailableRequestsRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/availableRequests");
    CustomRequestRegistrarImpl.addObserver(req ->
        struct.add(
            new AbstractMap.SimpleEntry<>(
                req.metadata().route(),
                new DescribedMetadata(req.metadata().type(), req.metadata().description()))));
  }

  public void populateDefaults(Map<String, MessengerHttpHandler> handlers) {
    for (Map.Entry<String, MessengerHttpHandler> entry : handlers.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("/")) {
        continue;
      }
      struct.add(
          new AbstractMap.SimpleEntry<>(
              entry.getKey(),
              new DescribedMetadata(
                  entry.getValue().metadata().type(), entry.getValue().metadata().description())));
    }
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    JsonArray array = new JsonArray();
    for (Map.Entry<String, DescribedMetadata> entry : struct) {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", entry.getValue().requestType().name());
      obj.addProperty("route", entry.getKey());
      if (entry.getValue().description() != null) {
        obj.addProperty("description", entry.getValue().description());
      }
      array.add(obj);
    }
    this.respond(exchange, array, 200);
  }
}
