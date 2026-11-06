package com.mrivanplays.webmessenger.api.request;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/** Represents a registrar for {@link CustomRequest custom requests} */
public interface CustomRequestRegistrar {

  /**
   * Register a new custom request
   *
   * @param request request to register
   * @throws IllegalArgumentException if the route given in the {@link RequestMetadata metadata} is
   *     already registered
   */
  void registerRequest(@NotNull CustomRequest request);

  /**
   * Check if a route is available
   *
   * @param route route to check
   * @return true if available, false otherwise
   */
  boolean isRouteAvailable(@NotNull String route);

  /**
   * Get the {@link RequestMetadata request metadata} of the provided route, if such {@link
   * CustomRequest custom request} is registered.
   *
   * @param route route to check
   * @return populated if present, empty if null
   */
  @NotNull
  Optional<RequestMetadata> metadataOf(@NotNull String route);
}
