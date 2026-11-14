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
package com.mrivanplays.webmessenger.core.apiimpl;

import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.api.event.CustomRequestUnregisterEvent;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomRequestRegistrarImpl implements CustomRequestRegistrar {

  @NullMarked
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
    Optional<RequestMetadata> maybeCurrent = this.metadataOf(request.metadata().route());
    if (maybeCurrent.isPresent()) {
      throw new IllegalArgumentException(
          "Route '"
              + request.metadata().route()
              + "' is already registered by "
              + maybeCurrent.get().identifier().asName());
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
      WebMessengerServer.getInstance()
          .eventBus()
          .fireAndForget(new CustomRequestUnregisterEvent(metadata));
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
