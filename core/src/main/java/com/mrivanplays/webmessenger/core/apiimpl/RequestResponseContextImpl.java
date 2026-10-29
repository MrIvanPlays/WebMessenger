package com.mrivanplays.webmessenger.core.apiimpl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import com.mrivanplays.webmessenger.core.util.Utils;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestResponseContextImpl implements RequestResponseContext {

  private final RequestMetadata metadata;
  private final HttpExchange exchange;
  private final Gson gson;
  private Integer httpCode;
  private final Map<String, String> attributes;
  private final List<CustomHeader> responseHeaders;
  private AtomicBoolean done = new AtomicBoolean(false);

  public RequestResponseContextImpl(HttpExchange exchange, Gson gson, RequestMetadata metadata) {
    this.exchange = exchange;
    this.gson = gson;
    this.attributes = Utils.getAttributes(exchange.getRequestURI());
    this.responseHeaders = new ArrayList<>();
    this.metadata = metadata;
  }

  @Override
  public void writeResponse(JsonElement response) throws IOException {
    if (response == null) {
      throw new NullPointerException(
          "Null response (route: '"
              + metadata.route()
              + "', identifier: '"
              + metadata.identifier().asName()
              + "')");
    }
    if (httpCode == null) {
      throw new NullPointerException(
          "Null http code (route: '"
              + metadata.route()
              + "', identifier: '"
              + metadata.identifier().asName()
              + "')");
    }
    String responseStr = gson.toJson(response);
    if (!responseHeaders.isEmpty()) {
      for (CustomHeader header : responseHeaders) {
        List<String> values = header.value();
        if (values.size() == 1) {
          exchange.getResponseHeaders().add(header.key(), values.getFirst());
        } else {
          exchange.getResponseHeaders().add(header.key(), String.join("; ", values));
        }
      }
    }

    byte[] strBytes = responseStr.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(httpCode, strBytes.length);
    exchange.getResponseBody().write(strBytes);
    exchange.close();
    done.set(true);
  }

  @Override
  public Optional<String> attribute(String name) {
    return Optional.ofNullable(this.attributes.get(name));
  }

  @Override
  public void setResponseCode(int httpCode) {
    if (done.get()) {
      throw new IllegalArgumentException(
          "Call 'setResponseCode' on done RequestResponseContext (route: '"
              + metadata.route()
              + "', identifier: '"
              + metadata.identifier().asName()
              + "')");
    }
    this.httpCode = httpCode;
  }

  @Override
  public Optional<InputStream> requestBody() {
    return Optional.ofNullable(exchange.getRequestBody());
  }

  @Override
  public Optional<CustomHeader> requestHeader(String key) {
    List<String> val = exchange.getRequestHeaders().get(key);
    if (val == null) {
      return Optional.empty();
    }
    return Optional.of(CustomHeader.of(key, val));
  }

  @Override
  public void addResponseHeader(CustomHeader header) {
    if (done.get()) {
      throw new IllegalArgumentException(
          "Call 'addResponseHeader' on done RequestResponseContext (route: '"
              + metadata.route()
              + "', identifier: '"
              + metadata.identifier().asName()
              + "')");
    }
    this.responseHeaders.add(header);
  }

  @Override
  public boolean isDone() {
    return this.done.get();
  }

  @Override
  public InetSocketAddress remoteAddress() {
    return exchange.getRemoteAddress();
  }

  @Override
  public RequestMetadata metadata() {
    return this.metadata;
  }
}
