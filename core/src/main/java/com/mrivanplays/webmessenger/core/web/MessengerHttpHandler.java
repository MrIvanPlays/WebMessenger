package com.mrivanplays.webmessenger.core.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.api.event.HttpRequestHandleEvent;
import com.mrivanplays.webmessenger.api.ratelimit.RateLimiter;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestType;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.apiimpl.RequestResponseContextImpl;
import com.mrivanplays.webmessenger.core.util.TwoValue;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public abstract class MessengerHttpHandler implements HttpHandler {

  protected Gson gson = new Gson();

  private RateLimiter rateLimiter;
  private final WebMessengerPlugin plugin;
  private final boolean shouldAuthenticate;

  public MessengerHttpHandler(WebMessengerPlugin plugin) {
    this(plugin, true);
  }

  public MessengerHttpHandler(WebMessengerPlugin plugin, boolean shouldAuthenticate) {
    this.plugin = plugin;
    this.shouldAuthenticate = shouldAuthenticate;
  }

  public abstract RequestMetadata metadata();

  protected abstract void handle0(HttpExchange exchange) throws IOException;

  @Override
  public void handle(HttpExchange exchange) {
    try {
      if (rateLimiter != null) {
        if (!rateLimiter.process(exchange.getRemoteAddress())) {
          this.handleGenericErrorResponse(exchange, "Too Many Requests", 429);
          return;
        }
        exchange
            .getResponseHeaders()
            .add(
                "X-Ratelimit-Remaining",
                String.valueOf(rateLimiter.remainingRequests(exchange.getRemoteAddress())));
        exchange
            .getResponseHeaders()
            .add("X-Ratelimit-Limit", String.valueOf(rateLimiter.settings().threshold()));
      }
      if (shouldAuthenticate) {
        if (!exchange.getRequestHeaders().containsKey("X-secret")) {
          this.handleGenericErrorResponse(exchange, "Forbidden", 403);
          return;
        }
        String obtainedSecret = exchange.getRequestHeaders().getFirst("X-secret");
        if (!obtainedSecret.equals(this.plugin.getConfig().getSecret())) {
          this.handleGenericErrorResponse(exchange, "Forbidden", 403);
          return;
        }
      }
      if ((this.metadata().type() != RequestType.ALL)
          && !exchange.getRequestMethod().equalsIgnoreCase(this.metadata().type().name())) {
        this.handleGenericErrorResponse(
            exchange,
            "Expected " + this.metadata().type().name() + ", got " + exchange.getRequestMethod(),
            400);
        return;
      }
      HttpRequestHandleEvent event =
          WebMessengerServer.getInstance()
              .eventBus()
              .fire(
                  new HttpRequestHandleEvent(
                      new RequestResponseContextImpl(exchange, this.gson, this.metadata())))
              .join();
      if (!event.result().allowed()) {
        return;
      }
      this.writeContentType(exchange);
      plugin.getLogger().debug("Performing request '" + metadata().route() + "'");
      handle0(exchange);
      plugin.getLogger().debug("Request '" + metadata().route() + "' performed successfully");
    } catch (Exception e) {
      plugin
          .getLogger()
          .error(
              "Exception during handling of request '"
                  + metadata().route()
                  + "' registered by '"
                  + metadata().identifier().asName()
                  + "'",
              e);
      try {
        handleGenericErrorResponse(exchange, "Internal server error", 500);
      } catch (IOException io) {
        plugin.getLogger().error("IO whilst trying to HTTP respond 500", io);
        exchange.close(); // assume exchange is not closed and close it.
      }
    }
  }

  protected WebMessengerPlugin getPlugin() {
    return this.plugin;
  }

  protected void handleGenericErrorResponse(HttpExchange exchange, String errorMessage, int code)
      throws IOException {
    JsonObject responseBody = new JsonObject();
    responseBody.addProperty("message", errorMessage);
    this.respond(exchange, responseBody, code);
  }

  protected void respond(
      HttpExchange exchange, CompletableFuture<JsonObject> response, Integer code) {
    response.whenComplete((resp, ex) -> {
      if (ex != null) {
        plugin.getLogger().error("Exception during response", ex);
        try {
          handleGenericErrorResponse(exchange, "Internal Server Error", 500);
        } catch (IOException e) {
          plugin.getLogger().error("IO while trying to respond", e);
        }
        return;
      }
      try {
        this.respond(exchange, resp, code);
      } catch (IOException e) {
        plugin.getLogger().error("IO while trying to respond", e);
      }
    });
  }

  protected void respond(
      HttpExchange exchange, CompletableFuture<TwoValue<JsonObject, Integer>> response) {
    response.whenComplete((resp, ex) -> {
      if (ex != null) {
        this.respond(exchange, CompletableFuture.failedFuture(ex), null);
        return;
      }
      this.respond(exchange, CompletableFuture.completedFuture(resp.first()), resp.second());
    });
  }

  protected void respond(HttpExchange exchange, JsonElement response, int code) throws IOException {
    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("code", code);
    responseObject.add("response", response);
    byte[] respByte = gson.toJson(responseObject).getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(code, respByte.length);
    exchange.getResponseBody().write(respByte);
    exchange.close();
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  protected void writeContentType(HttpExchange exchange) {
    if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
      exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    }
  }

  protected JsonObject readJsonObject(HttpExchange exchange) throws IOException {
    if (exchange.getRequestBody() == null) {
      handleGenericErrorResponse(exchange, "Expected JSON body for POST, got null", 400);
      return null;
    }
    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    JsonObject object;
    try {
      object = gson.fromJson(body, JsonObject.class);
    } catch (JsonSyntaxException e) {
      this.handleGenericErrorResponse(
          exchange, "Bad request: JsonSyntaxException '" + e.getMessage() + "'", 400);
      return null;
    }
    return object;
  }
}
