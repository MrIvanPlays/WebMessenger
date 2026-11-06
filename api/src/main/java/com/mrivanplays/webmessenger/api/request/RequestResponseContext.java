package com.mrivanplays.webmessenger.api.request;

import com.google.gson.JsonElement;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

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
public interface RequestResponseContext {

  /**
   * Writes a {@link JsonElement JSON} response to the request.
   *
   * @param response response to write
   * @throws IOException if an i/o error occurs
   * @throws NullPointerException if the response is null, or the response code has not been set
   */
  void writeResponse(@NotNull JsonElement response) throws IOException;

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
  Optional<String> attribute(@NotNull String name);

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
   * Get a header (if available) which the request contains.
   *
   * @param key request header key
   * @return populated optional if present, empty optional otherwise
   */
  Optional<CustomHeader> requestHeader(@NotNull String key);

  /**
   * Add a custom header to the response when written
   *
   * @param header custom header to add
   * @throws IllegalArgumentException if done ( {@link #isDone()} )
   */
  void addResponseHeader(@NotNull CustomHeader header);

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
  @NotNull
  InetSocketAddress remoteAddress();

  /**
   * Get the request metadata this context was constructed from.
   *
   * @return request metadata
   */
  @NotNull
  RequestMetadata metadata();
}
