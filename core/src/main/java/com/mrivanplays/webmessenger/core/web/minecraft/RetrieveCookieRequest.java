package com.mrivanplays.webmessenger.core.web.minecraft;

import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.util.Either;
import com.mrivanplays.webmessenger.core.util.TwoValue;
import com.mrivanplays.webmessenger.core.util.Utils;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RetrieveCookieRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public RetrieveCookieRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata = RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/retrieveCookie");
  }

  @Override
  public RequestMetadata metadata() {
    return this.metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    var attributes = Utils.getAttributes(exchange.getRequestURI());
    var nameAttr = attributes.get("name");
    var uuidAttr = attributes.get("uuid");

    if (nameAttr == null && uuidAttr == null) {
      super.handleGenericErrorResponse(exchange, "No name or uuid of a player provided", 404);
      return;
    }
    var cookieIdAttr = attributes.get("cookieId");
    if (cookieIdAttr == null) {
      super.handleGenericErrorResponse(exchange, "No cookie id provided", 404);
      return;
    }
    Either<String, UUID> reqInfo;
    if (nameAttr != null) {
      reqInfo = Either.left(nameAttr);
    } else {
      try {
        reqInfo = Either.right(UUID.fromString(uuidAttr));
      } catch (IllegalArgumentException e) {
        super.handleGenericErrorResponse(exchange, "Invalid uuid provided", 400);
        return;
      }
    }
    this.respond(exchange, this.createResponseBody(reqInfo, cookieIdAttr));
  }

  private CompletableFuture<TwoValue<JsonObject, Integer>> createResponseBody(
      Either<String, UUID> reqInfo, String cookieIdAttr) {
    return this.getPlugin()
        .getDataProvider()
        .retrieveCookieFor(reqInfo, cookieIdAttr)
        .thenApply(b -> {
          JsonObject response = new JsonObject();
          if (b == null) {
            response.addProperty(
                "message", "No cookie " + cookieIdAttr + " found for the provided player");
            return new TwoValue<>(response, 404);
          }
          response.addProperty("cookie", Base64.getEncoder().encodeToString(b));
          return new TwoValue<>(response, 200);
        });
  }
}
