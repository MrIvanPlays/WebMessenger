package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class RobotsTxtEndpoint extends MessengerHttpHandler {

  private final RequestMetadata metadata;
  private byte[] cachedBytes;

  public RobotsTxtEndpoint(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata = RequestMetadata.simpleGet(WebMessengerPlugin.API_IDENTIFIER, "/robots.txt");
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    if (cachedBytes == null) {
      cachedBytes = getClass().getClassLoader().getResourceAsStream("robots.txt").readAllBytes();
    }
    exchange.sendResponseHeaders(200, cachedBytes.length);
    exchange.getResponseBody().write(cachedBytes);
    exchange.close();
  }

  @Override
  protected boolean shouldAuthenticate() {
    return false;
  }

  @Override
  protected void writeContentType(HttpExchange exchange) {
    if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
      exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    }
  }
}
