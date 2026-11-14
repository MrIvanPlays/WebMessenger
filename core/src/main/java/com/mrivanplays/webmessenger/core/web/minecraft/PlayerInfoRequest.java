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
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.util.Utils;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public PlayerInfoRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simpleGet(
            WebMessengerPlugin.API_IDENTIFIER,
            "/playerInfo",
            "Get information on player. Usage: /playerInfo?<name or uuid>=<name or uuid of player>");
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    Map<String, String> attributes = Utils.getAttributes(exchange.getRequestURI());
    String nameAttr = attributes.get("name");
    String uuidAttr = attributes.get("uuid");

    if (nameAttr == null && uuidAttr == null) {
      super.handleGenericErrorResponse(exchange, "No name or uuid of a player provided", 404);
      return;
    }
    PlayerInfo playerInfo;
    if (nameAttr == null) {
      try {
        playerInfo = this.getPlugin().getDataProvider().playerInfo(UUID.fromString(uuidAttr));
      } catch (IllegalArgumentException e) {
        this.handleGenericErrorResponse(exchange, "Invalid uuid provided", 400);
        return;
      }
    } else {
      playerInfo = this.getPlugin().getDataProvider().playerInfo(nameAttr);
    }
    if (playerInfo == null) {
      this.handleGenericErrorResponse(exchange, "Not Found", 404);
      return;
    }
    String server = "server_no_proxy";
    if (getPlugin().getDataProvider().type().isProxy()
        && playerInfo.connectedServerInfo() != null) {
      server = playerInfo.connectedServerInfo().name();
    }

    JsonObject responseBody = new JsonObject();
    responseBody.addProperty("name", playerInfo.name());
    responseBody.addProperty("uuid", playerInfo.uuid().toString());
    responseBody.addProperty("server", server);
    super.respond(exchange, responseBody, 200);
  }
}
