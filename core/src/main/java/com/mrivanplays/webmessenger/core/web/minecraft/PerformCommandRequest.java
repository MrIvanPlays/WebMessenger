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
package com.mrivanplays.webmessenger.core.web.minecraft;

import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class PerformCommandRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public PerformCommandRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simplePost(
            WebMessengerPlugin.API_IDENTIFIER,
            "/executeCommand",
            "Perform a command of your choice on the server. Expected JSON syntax on POST: {\"command\":\"<command to run>\"}");
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  // expected payload: "{"command":"<command to run>"}"
  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    JsonObject object = this.readJsonObject(exchange);
    if (object == null) {
      // readJsonObject handled error already, return
      return;
    }
    if (object.isEmpty() || !object.has("command")) {
      handleGenericErrorResponse(exchange, "Nothing provided to execute", 400);
      return;
    }
    String command = object.get("command").getAsString();
    getPlugin()
        .getDataProvider()
        .performCommand(command)
        .whenComplete((resp, e) -> {
          if (e != null) {
            getPlugin().getLogger().error("Exception when performing command", e);
            return;
          }
          JsonObject response = new JsonObject();
          try {
            if (resp) {
              response.addProperty("message", "Success");
              this.respond(exchange, response, 200);
            } else {
              response.addProperty("message", "Command execution failed");
              this.respond(exchange, response, 502);
            }
          } catch (IOException ex) {
            getPlugin().getLogger().error("Exception when responding", ex);
          }
        });
  }
}
