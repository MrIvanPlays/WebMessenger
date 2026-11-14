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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RequestResponseContextImpl implements RequestResponseContext {

  private final RequestMetadata metadata;
  private final HttpExchange exchange;
  private final Gson gson;
  private @Nullable Integer httpCode;
  private final Map<String, String> attributes;
  private final List<CustomHeader> responseHeaders;
  private final AtomicBoolean done = new AtomicBoolean(false);
  private final String usedRequestType;

  public RequestResponseContextImpl(HttpExchange exchange, Gson gson, RequestMetadata metadata) {
    this.exchange = exchange;
    this.gson = gson;
    this.attributes = Utils.getAttributes(exchange.getRequestURI());
    this.responseHeaders = new ArrayList<>();
    this.metadata = metadata;
    this.usedRequestType = exchange.getRequestMethod();
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
        if (values == null || values.isEmpty()) {
          continue;
        }
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
    Objects.requireNonNull(name, "name");
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
    Objects.requireNonNull(key, "key");
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
    Objects.requireNonNull(header, "header");
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

  @Override
  public String requestUsedType() {
    return this.usedRequestType;
  }
}
