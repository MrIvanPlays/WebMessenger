package com.mrivanplays.webmessenger.api.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Represents a registrar for {@link CustomRequest custom requests} */
public enum CustomRequestRegistrar {
  INSTANCE;

  private List<CustomRequest> registrar = new ArrayList<>();

  /**
   * Register a new custom request
   *
   * @param request request to register
   * @throws IllegalArgumentException if the route given in the {@link RequestMetadata metadata} is
   *     already registered
   */
  public void registerRequest(CustomRequest request) {
    if (!isRouteAvailable(request.metadata().route())) {
      throw new IllegalArgumentException(
          "Route '"
              + request.metadata().route()
              + "' is already registered by "
              + metadataOf(request.metadata().route()).get().identifier().asName());
    }
    registrar.add(new CustomRequestWrapper(request));
  }

  /**
   * Check if a route is available
   *
   * @param route route to check
   * @return true if available, false otherwise
   */
  public boolean isRouteAvailable(String route) {
    return registrar.stream().noneMatch(c -> c.metadata().route().equalsIgnoreCase(route));
  }

  /**
   * Get the {@link RequestMetadata request metadata} of the provided route, if such {@link
   * CustomRequest custom request} is registered.
   *
   * @param route route to check
   * @return populated if present, empty if null
   */
  public Optional<RequestMetadata> metadataOf(String route) {
    return registrar.stream()
        .filter(c -> c.metadata().route().equalsIgnoreCase(route))
        .findFirst()
        .map(CustomRequest::metadata);
  }

  /**
   * Get an unmodifiable {@link List} copy of the registered {@link CustomRequest custom requests}
   *
   * @return list of registered requests
   */
  public List<CustomRequest> getRegistrar() {
    return Collections.unmodifiableList(registrar);
  }
}
