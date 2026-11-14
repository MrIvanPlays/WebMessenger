/*
 * Copyright (c) Ivan Pekov 2026
 * Copyright (c) Contributors 2026
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mrivanplays.webmessenger.core.web.generic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
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
import org.jspecify.annotations.NonNull;

public class AvailableRequestsRequest extends MessengerHttpHandler {

  record DescribedMetadata(RequestType requestType, String description) {}

  private final RequestMetadata metadata;
  private final Set<Map.Entry<String, DescribedMetadata>> struct =
      Collections.newSetFromMap(new ConcurrentHashMap<>());

  public AvailableRequestsRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/availableRequests");
    CustomRequestRegistrarImpl.addObserver(
        new CustomRequestRegistrarImpl.Observer() {
          @Override
          public void onRequestRegistered(@NonNull CustomRequest req) {
            struct.add(
                new AbstractMap.SimpleEntry<>(
                    req.metadata().route(),
                    new DescribedMetadata(req.metadata().type(), req.metadata().description())));
          }

          @Override
          public void onRequestUnregistered(@NonNull RequestMetadata metadata) {
            struct.removeIf(e ->
                e.getKey().equals(metadata.route())
                    && (e.getValue().description() == null
                        || metadata.description() == null
                        || e.getValue().description().equals(metadata.description()))
                    && e.getValue().requestType() == metadata.type());
          }
        });
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
