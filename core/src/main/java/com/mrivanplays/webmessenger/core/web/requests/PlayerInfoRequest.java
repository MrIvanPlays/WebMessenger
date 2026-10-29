package com.mrivanplays.webmessenger.core.web.requests;

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
    this.metadata = RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/playerInfo");
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
      playerInfo = this.getPlugin().getDataProvider().playerInfo(UUID.fromString(uuidAttr));
    } else {
      playerInfo = this.getPlugin().getDataProvider().playerInfo(nameAttr);
    }
    if (playerInfo == null) {
      this.handleGenericErrorResponse(exchange, "Not Found", 404);
      return;
    }
    String server = "server_no_proxy";
    if (getPlugin().getDataProvider().isProxy() && playerInfo.connectedServerInfo() != null) {
      server = playerInfo.connectedServerInfo().name();
    }

    JsonObject responseBody = new JsonObject();
    responseBody.addProperty("name", playerInfo.name());
    responseBody.addProperty("uuid", playerInfo.uuid().toString());
    responseBody.addProperty("server", server);
    super.respond(exchange, responseBody, 200);
  }
}
