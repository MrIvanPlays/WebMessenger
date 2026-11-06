package com.mrivanplays.webmessenger.core.web.minecraft;

import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.util.Either;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class StoreCookieRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;
  private static final String[] EXPECTED_BODY_KEYS =
      new String[] {"cookieId", "data", "forPlayer", "playerDataType"};

  public StoreCookieRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata = RequestMetadata.simplePost(WebMessengerPlugin.API_IDENTIFIER, "/storeCookie");
  }

  @Override
  public RequestMetadata metadata() {
    return this.metadata;
  }

  // expected payload: "{"cookieId":"<cookie:id>","data":"<base64 encoded cookie
  // data>","forPlayer":"<player name or uuid>","playerDataType":"<uuid or name>"}"
  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    JsonObject object = this.readJsonObject(exchange);
    if (object == null) return; // exchange is already handled by readJsonObject
    boolean fail = false;
    var failedAt = "";
    for (var expected : EXPECTED_BODY_KEYS) {
      if (!object.has(expected)) {
        fail = true;
        failedAt = expected;
        break;
      }
    }
    if (fail) {
      this.handleGenericErrorResponse(
          exchange, "Expected " + failedAt + " to be present in POST body, got null", 400);
      return;
    }
    var info = this.resolveRequestInfo(object);
    if (info == null) {
      this.handleGenericErrorResponse(
          exchange,
          "Expected NAME or UUID for playerDataType, got: "
              + object.get("playerDataType").getAsString(),
          400);
      return;
    }
    String cookieId = object.get("cookieId").getAsString();
    if (!cookieId.contains(":")) {
      this.handleGenericErrorResponse(
          exchange, "Expected key:value for cookie id, got " + cookieId, 400);
      return;
    }
    byte[] data;
    try {
      data = Base64.getDecoder().decode(object.get("data").getAsString());
    } catch (IllegalArgumentException e) {
      this.handleGenericErrorResponse(
          exchange, "Provided data is not in a valid Base64 scheme", 400);
      return;
    }
    JsonObject response = new JsonObject();
    int code;
    if (getPlugin().getDataProvider().storeCookie(info, cookieId, data)) {
      response.addProperty("message", "Cookie " + cookieId + " has been stored successfully");
      code = 200;
    } else {
      response.addProperty("message", "Cannot store cookie: player not found");
      code = 404;
    }
    this.respond(exchange, response, code);
  }

  private Either<String, UUID> resolveRequestInfo(JsonObject object) {
    var dataType = object.get("playerDataType").getAsString();
    var parse = object.get("forPlayer").getAsString();
    if (dataType.equalsIgnoreCase("uuid")) {
      try {
        return Either.right(UUID.fromString(parse));
      } catch (IllegalArgumentException e) {
        return null;
      }
    } else if (dataType.equalsIgnoreCase("name")) {
      return Either.left(parse);
    } else {
      return null;
    }
  }
}
