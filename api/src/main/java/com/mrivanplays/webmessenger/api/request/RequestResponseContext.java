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
package com.mrivanplays.webmessenger.api.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a response context for a received request.
 *
 * <p>Whenever modifying a context, <b>ALWAYS</b> {@link #writeResponse(JsonElement)} shall be
 * called <b>LAST</b>.
 *
 * <p>An example usage of context is as follows: <code>
 *   ...
 *   public void handle(RequestResponseContext ctx) {
 *     ctx.addResponseHeader(CustomHeader.ofSingleValue("X-my-custom-header", ctx.metadata().identifier().asName()));
 *     ctx.setResponseCode(200);
 *     JsonObject resp = new JsonObject();
 *     resp.addProperty("hello", "this is my custom response");
 *     ctx.writeResponse(resp);
 *   }
 *   ...
 *   </code>
 *
 * <p>A done RequestResponseContext is not able to do anything, as the original http exchange is
 * closed whenever a response has been written to the requestee.
 */
@NullMarked
public interface RequestResponseContext {

  /**
   * Writes a {@link JsonElement JSON} response to the request.
   *
   * @param response response to write
   * @throws IOException if an i/o error occurs
   * @throws NullPointerException if the response is null, or the response code has not been set
   */
  void writeResponse(JsonElement response) throws IOException;

  /**
   * Get a url attribute if present.
   *
   * <p>An attribute represents the information appended to the url via "?" For example a request of
   * <code>/myRequest?name=IAmCool&myCustomParam=1</code> will result in attributes <code>name
   * </code> and <code>myCustomParam</code> present with values of <code>IAmCool</code> and <code>1
   * </code>.
   *
   * @param name attribute name
   * @return populated optional if present, empty optional otherwise
   */
  Optional<String> attribute(String name);

  /**
   * Set the response code before writing a response body
   *
   * @param httpCode response code (ex. 200 - success, 404 - not found, 403 - forbidden)
   * @throws IllegalArgumentException if done ( {@link #isDone()} )
   */
  void setResponseCode(int httpCode);

  /**
   * Get the request body if present.
   *
   * @return populated optional if present, empty optional otherwise
   */
  Optional<InputStream> requestBody();

  /**
   * Read the request body as {@link JsonObject json object} if it's present.
   *
   * @param gson Gson instance to parse from
   * @return populated optional if present, empty optional otherwise
   * @throws IOException if an i/o error occurs
   */
  default Optional<JsonObject> readRequestBodyAsJson(Gson gson) throws IOException {
    Objects.requireNonNull(gson, "gson");
    var body = this.requestBody();
    if (body.isEmpty()) {
      return Optional.empty();
    }
    var stream = body.get();
    return Optional.of(
        gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), JsonObject.class));
  }

  /**
   * Get a header (if available) which the request contains.
   *
   * @param key request header key
   * @return populated optional if present, empty optional otherwise
   */
  Optional<CustomHeader> requestHeader(String key);

  /**
   * Utility method to directly check whether a request header is present
   *
   * @param key request header key
   * @return true if present, false otherwise
   */
  default boolean hasRequestHeader(String key) {
    return requestHeader(key).isPresent();
  }

  /**
   * Add a custom header to the response when written
   *
   * @param header custom header to add
   * @throws IllegalArgumentException if done ( {@link #isDone()} )
   */
  void addResponseHeader(CustomHeader header);

  /**
   * Returns whether this response context was already handled.
   *
   * <p>Contract is, if {@link #writeResponse(JsonElement)} was not called at the time of checking,
   * this will always return <code>false</code>, otherwise checked after <code>writeResponse</code>
   * is called, will return <code>true</code>
   *
   * @return whether this context has written a response already
   */
  boolean isDone();

  /**
   * Get the remote address which did the request
   *
   * @return remote address
   */
  InetSocketAddress remoteAddress();

  /**
   * Get the request metadata this context was constructed from.
   *
   * @return request metadata
   */
  RequestMetadata metadata();

  /**
   * Get the request type method this request used.
   *
   * @return request type method string
   */
  String requestUsedType();

  /**
   * Get the request type method this request used as a {@link RequestType request type} constant.
   *
   * <p>May return <code>null</code>, since not all HTTP method request types are present in the
   * request type enum.
   *
   * @return request type method enum, or null
   */
  default @Nullable RequestType requestUsedTypeParsed() {
    try {
      return RequestType.valueOf(this.requestUsedType().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
