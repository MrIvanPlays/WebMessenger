package com.mrivanplays.webmessenger.core.apiimpl;

import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CustomRequestRegistrarImpl implements CustomRequestRegistrar {

  public interface Observer {

    void onRequestRegistered(CustomRequest request);

    void onRequestUnregistered(RequestMetadata metadata);
  }

  private final Set<CustomRequest> registrar = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private static final Set<Observer> observers =
      Collections.newSetFromMap(new ConcurrentHashMap<>());

  public static void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void registerRequest(CustomRequest request) {
    Objects.requireNonNull(request, "request");
    if (!isRouteAvailable(request.metadata().route())) {
      throw new IllegalArgumentException(
          "Route '"
              + request.metadata().route()
              + "' is already registered by "
              + metadataOf(request.metadata().route()).get().identifier().asName());
    }
    var wrapped = new CustomRequestWrapper(request);
    registrar.add(wrapped);
    for (Observer observer : observers) {
      observer.onRequestRegistered(wrapped);
    }
  }

  @Override
  public void unregisterRequest(RequestMetadata metadata) {
    Objects.requireNonNull(metadata, "metadata");
    if (this.registrar.removeIf(
        r ->
            r.metadata().identifier().equals(metadata.identifier())
                && r.metadata().route().equals(metadata.route())
                && r.metadata().type() == metadata.type())) {
      for (Observer observer : observers) {
        observer.onRequestUnregistered(metadata);
      }
    }
  }

  @Override
  public boolean isRouteAvailable(String route) {
    Objects.requireNonNull(route, "route");
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
    return registrar.stream().noneMatch(c -> c.metadata().route().equalsIgnoreCase(route));
  }

  @Override
  public Optional<RequestMetadata> metadataOf(String route) {
    Objects.requireNonNull(route, "route");
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
    return registrar.stream()
        .filter(c -> c.metadata().route().equalsIgnoreCase(route))
        .findFirst()
        .map(CustomRequest::metadata);
  }
}
