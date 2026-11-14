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
package com.mrivanplays.webmessenger.core.web.generic;

import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class RobotsTxtEndpoint extends MessengerHttpHandler {

  private final RequestMetadata metadata;
  private byte[] cachedBytes;

  public RobotsTxtEndpoint(WebMessengerPlugin plugin) {
    super(plugin, false);
    this.metadata =
        RequestMetadata.simpleGet(
            WebMessengerPlugin.API_IDENTIFIER, "/robots.txt", "Endpoint for robots.");
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
  protected void writeContentType(HttpExchange exchange) {
    if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
      exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    }
  }
}
