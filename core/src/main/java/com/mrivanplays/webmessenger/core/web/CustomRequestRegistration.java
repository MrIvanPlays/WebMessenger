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
package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.apiimpl.RequestResponseContextImpl;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

public class CustomRequestRegistration extends MessengerHttpHandler {
  private final CustomRequest request;

  public CustomRequestRegistration(WebMessengerPlugin plugin, CustomRequest request) {
    super(plugin, request.metadata().shallAuthenticate());
    this.request = request;
  }

  @Override
  public RequestMetadata metadata() {
    return request.metadata();
  }

  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    List<CustomHeader> requiredHeaders = request.metadata().requiredHeaders();
    if (requiredHeaders != null && !requiredHeaders.isEmpty()) {
      boolean shallHandle = true;
      for (CustomHeader header : requiredHeaders) {
        List<String> values = exchange.getRequestHeaders().getOrDefault(header.key(), null);
        if (values == null || header.value() == null) {
          shallHandle = false;
          break;
        }
        if (!header.value().equals(values)) {
          shallHandle = false;
          break;
        }
      }
      if (!shallHandle) {
        this.handleGenericErrorResponse(exchange, "Invalid or missing request headers", 400);
        return;
      }
    }

    request.handle(new RequestResponseContextImpl(exchange, gson, request.metadata()));
  }
}
