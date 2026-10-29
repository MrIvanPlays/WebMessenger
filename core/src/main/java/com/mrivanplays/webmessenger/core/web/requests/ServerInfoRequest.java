package com.mrivanplays.webmessenger.core.web.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.util.Utils;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.management.RuntimeErrorException;

public class ServerInfoRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public ServerInfoRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata = RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/serverInfo");
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    Map<String, String> attributes = Utils.getAttributes(exchange.getRequestURI());
    String serverNameAttr = attributes.get("serverName");
    if (serverNameAttr == null) {
      this.respond(
          exchange,
          createResponseBody(getPlugin().getDataProvider())
              .thenApply(
                  obj -> {
                    obj.addProperty(
                        "serverSoftwareType",
                        getPlugin().getDataProvider().type().name().toLowerCase(Locale.ROOT));
                    return obj;
                  }),
          200);
    } else {
      if (!getPlugin().getDataProvider().isProxy()) {
        this.handleGenericErrorResponse(
            exchange,
            "Server is not a proxy and cannot provide information for server :" + serverNameAttr,
            403);
        return;
      }
      ServerInfo serverInfo = getPlugin().getDataProvider().serverInfo(serverNameAttr);
      if (serverInfo == null) {
        this.handleGenericErrorResponse(exchange, "Cannot find server: " + serverNameAttr, 404);
        return;
      }
      this.respond(exchange, this.createResponseBody(serverInfo), 200);
    }
  }

  private CompletableFuture<JsonObject> createResponseBody(ServerInfo serverInfo) {
    return serverInfo
        .ping()
        .thenApply(
            (ping) -> {
              if (ping == null) {
                ping = new ServerPing("unknown", false, "unknown");
              }
              JsonObject responseBody = new JsonObject();
              responseBody.addProperty("onlinePlayers", serverInfo.onlinePlayers());
              responseBody.addProperty("serverVersion", ping.version());
              responseBody.addProperty("motd", ping.motd());
              responseBody.addProperty("online", ping.online());
              JsonArray players = new JsonArray();
              for (PlayerInfo playerInfo : getPlugin().getDataProvider().connectedPlayers()) {
                JsonObject playerObj = new JsonObject();
                playerObj.addProperty("name", playerInfo.name());
                playerObj.addProperty("uuid", playerInfo.uuid().toString());
                players.add(playerObj);
              }
              responseBody.add("players", players);
              return responseBody;
            });
  }
}
