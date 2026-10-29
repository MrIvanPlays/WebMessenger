package com.mrivanplays.webmessenger.api.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum CustomRequestRegistrar {
  INSTANCE;

  private List<CustomRequest> registrar = new ArrayList<>();

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

  public boolean isRouteAvailable(String route) {
    return registrar.stream().noneMatch(c -> c.metadata().route().equalsIgnoreCase(route));
  }

  public Optional<RequestMetadata> metadataOf(String route) {
    return registrar.stream()
        .filter(c -> c.metadata().route().equalsIgnoreCase(route))
        .findFirst()
        .map(CustomRequest::metadata);
  }

  public List<CustomRequest> getRegistrar() {
    return Collections.unmodifiableList(registrar);
  }
}
