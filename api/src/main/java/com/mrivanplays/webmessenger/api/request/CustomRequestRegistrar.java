package com.mrivanplays.webmessenger.api.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/** Represents a registrar for {@link CustomRequest custom requests} */
public enum CustomRequestRegistrar {
  INSTANCE;

  private Set<CustomRequest> registrar = Collections.newSetFromMap(new ConcurrentHashMap<>());

  /**
   * Register a new custom request
   *
   * @param request request to register
   * @throws IllegalArgumentException if the route given in the {@link RequestMetadata metadata} is
   *     already registered
   */
  public void registerRequest(@NotNull CustomRequest request) {
    Objects.requireNonNull(request, "request");
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
  public boolean isRouteAvailable(@NotNull String route) {
    Objects.requireNonNull(route, "route");
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
    return registrar.stream().noneMatch(c -> c.metadata().route().equalsIgnoreCase(route));
  }

  /**
   * Get the {@link RequestMetadata request metadata} of the provided route, if such {@link
   * CustomRequest custom request} is registered.
   *
   * @param route route to check
   * @return populated if present, empty if null
   */
  @NotNull
  public Optional<RequestMetadata> metadataOf(@NotNull String route) {
    Objects.requireNonNull(route, "route");
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
    return registrar.stream()
        .filter(c -> c.metadata().route().equalsIgnoreCase(route))
        .findFirst()
        .map(CustomRequest::metadata);
  }

  /**
   * Get an {@link List} copy of the registered {@link CustomRequest custom requests}. Modifying the
   * returned list will NOT register a new request.
   *
   * @return list of registered requests
   */
  public List<CustomRequest> getRegistrar() {
    return new ArrayList<>(registrar);
  }
}
