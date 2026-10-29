package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.Arrays;
import java.util.List;

/** Represents a holder of metadata for a {@link CustomRequest custom request} */
public interface RequestMetadata {

  /**
   * Create a simple metadata with a {@link RequestType#GET GET} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  static RequestMetadata simpleGet(Identifier identifier, String route) {
    return of(identifier, route, RequestType.GET);
  }

  /**
   * Create a simple metadata with a {@link RequestType#POST POST} request type
   *
   * @param identifier metadata creator identifier
   * @param route route that the web server handles
   * @return new request metadata
   */
  static RequestMetadata simplePost(Identifier identifier, String route) {
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
  static RequestMetadata of(
      Identifier identifier, String route, RequestType type, CustomHeader... headers) {
    return of(identifier, route, type, Arrays.asList(headers));
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
  static RequestMetadata of(
      Identifier identifier, String route, RequestType type, List<CustomHeader> headers) {
    return new RequestMetadata() {
      @Override
      public Identifier identifier() {
        return identifier;
      }

      @Override
      public String route() {
        return route;
      }

      @Override
      public RequestType type() {
        return type;
      }

      @Override
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
  Identifier identifier();

  /**
   * Get the route the web server is going to handle when this metadata is registered in a {@link
   * CustomRequest custom request}
   *
   * @return route
   */
  String route();

  /**
   * Get the {@link RequestType request type}
   *
   * @return request type
   */
  RequestType type();

  /**
   * Get the required {@link CustomHeader custom headers} for a {@link CustomRequest custom request}
   * before a request is passed to {@link CustomRequest#handle}
   *
   * @return required headers
   */
  List<CustomHeader> requiredHeaders();
}
