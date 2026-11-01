package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a holder of metadata for a {@link CustomRequest custom request} */
public interface RequestMetadata {

  /**
   * Create a simple metadata with a {@link RequestType#GET GET} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  @NotNull
  static RequestMetadata simpleGet(@NotNull Identifier identifier, @NotNull String route) {
    return of(identifier, route, RequestType.GET);
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
    return of(identifier, route, RequestType.POST);
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
    Objects.requireNonNull(identifier, "identifier");
    Objects.requireNonNull(route, "route");
    Objects.requireNonNull(type, "type");
    if (route.isEmpty()) {
      throw new IllegalArgumentException("route cannot be empty");
    }
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
    return new RequestMetadata() {
      @Override
      @NotNull
      public Identifier identifier() {
        return identifier;
      }

      @Override
      @NotNull
      public String route() {
        return route;
      }

      @Override
      @NotNull
      public RequestType type() {
        return type;
      }

      @Override
      @Nullable
      public List<CustomHeader> requiredHeaders() {
        return headers;
      }
    };
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
}
