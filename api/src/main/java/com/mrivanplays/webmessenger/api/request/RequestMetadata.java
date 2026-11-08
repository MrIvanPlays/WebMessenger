package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a holder of metadata for a {@link CustomRequest custom request} */
@ApiStatus.NonExtendable
public sealed interface RequestMetadata permits RequestMetadataImpl {

  /**
   * Create a simple metadata with a {@link RequestType#GET GET} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata simpleGet(@NotNull Identifier identifier, @NotNull String route) {
    return simpleGet(identifier, route, null);
  }

  /**
   * Create a simple metadata with a {@link RequestType#GET GET} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata simpleGet(
      @NotNull Identifier identifier, @NotNull String route, @Nullable String description) {
    return of(identifier, route, RequestType.GET, description);
  }

  /**
   * Create a simple metadata with a {@link RequestType#POST POST} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata simplePost(@NotNull Identifier identifier, @NotNull String route) {
    return simplePost(identifier, route, null);
  }

  /**
   * Create a simple metadata with a {@link RequestType#POST POST} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param description description
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata simplePost(
      @NotNull Identifier identifier, @NotNull String route, @Nullable String description) {
    return of(identifier, route, RequestType.POST, description);
  }

  /**
   * Creates a new metadata
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param type request type
   * @param headers required headers before the request is passed to {@link CustomRequest#handle}
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata of(
      @NotNull Identifier identifier,
      @NotNull String route,
      @NotNull RequestType type,
      @Nullable CustomHeader... headers) {
    return of(identifier, route, type, headers == null ? List.of() : Arrays.asList(headers));
  }

  /**
   * Creates a new metadata
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param type request type
   * @param description description
   * @param headers required headers before the request is passed to {@link CustomRequest#handle}
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata of(
      @NotNull Identifier identifier,
      @NotNull String route,
      @NotNull RequestType type,
      @Nullable String description,
      @Nullable CustomHeader... headers) {
    return of(
        identifier, route, type, description, headers == null ? List.of() : Arrays.asList(headers));
  }

  /**
   * Creates a new metadata
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param type request type
   * @param headers required headers before the request is passed to {@link CustomRequest#handle}
   * @return new request metadata
   * @throws NullPointerException if the identifier, the route or the request type are null
   * @throws IllegalArgumentException if the route is empty or doesn't start with '/'
   */
  @NotNull
  static RequestMetadata of(
      @NotNull Identifier identifier,
      @NotNull String route,
      @NotNull RequestType type,
      @Nullable List<CustomHeader> headers) {
    return of(identifier, route, type, null, headers);
  }

  /**
   * Creates a new metadata
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param type request type
   * @param description request description
   * @param headers required headers before the request is passed to {@link CustomRequest#handle}
   * @return new request metadata
   * @throws NullPointerException if the identifier, the route or the request type are null
   * @throws IllegalArgumentException if the route is empty or doesn't start with '/'
   */
  @NotNull
  static RequestMetadata of(
      @NotNull Identifier identifier,
      @NotNull String route,
      @NotNull RequestType type,
      @Nullable String description,
      @Nullable List<CustomHeader> headers) {
    return of(identifier, route, type, description, true, headers);
  }

  /**
   * Creates a new metadata
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @param type request type
   * @param description request description
   * @param shallAuthenticate shall the request be authenticated by WebMessenger server
   * @param headers required headers before the request is passed to {@link CustomRequest#handle}
   * @return new request metadata
   * @throws NullPointerException if the identifier, the route or the request type are null
   * @throws IllegalArgumentException if the route is empty or doesn't start with '/'
   */
  @NotNull
  static RequestMetadata of(
      @NotNull Identifier identifier,
      @NotNull String route,
      @NotNull RequestType type,
      @Nullable String description,
      boolean shallAuthenticate,
      @Nullable List<CustomHeader> headers) {
    return new RequestMetadataImpl(identifier, route, type, headers, description, shallAuthenticate);
  }

  /**
   * Get the {@link Identifier identifier} creator of this metadata.
   *
   * @return identifier
   */
  @NotNull
  Identifier identifier();

  /**
   * Get the route the web server is going to handle when this metadata is registered in a {@link
   * CustomRequest custom request}
   *
   * @return route
   */
  @NotNull
  String route();

  /**
   * Get the {@link RequestType request type}
   *
   * @return request type
   */
  @NotNull
  RequestType type();

  /**
   * Get the required {@link CustomHeader custom headers} for a {@link CustomRequest custom request}
   * before a request is passed to {@link CustomRequest#handle}
   *
   * @return required headers
   */
  @Nullable
  List<CustomHeader> requiredHeaders();

  /**
   * Get basic description of what the request this metadata is for is doing.
   *
   * <p>An example to include in a description is usage ( "usage: /myCustomRequest?name=[name
   * here]&customParam=[custom param]" )
   *
   * @return description, may be null
   */
  @Nullable
  String description();

  /**
   * Returns whether the request this metadata is for is to be authenticated by the WebMessenger server.
   *
   * @return shall authenticate
   */
  boolean shallAuthenticate();
}
