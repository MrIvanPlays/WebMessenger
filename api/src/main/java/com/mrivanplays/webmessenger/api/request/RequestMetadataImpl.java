package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record RequestMetadataImpl(
    @NotNull Identifier identifier,
    @NotNull String route,
    @NotNull RequestType type,
    @Nullable List<CustomHeader> requiredHeaders,
    @Nullable String description)
    implements RequestMetadata {

  RequestMetadataImpl {
    Objects.requireNonNull(identifier, "identifier");
    Objects.requireNonNull(route, "route");
    Objects.requireNonNull(type, "type");
    if (route.isEmpty()) {
      throw new IllegalArgumentException("route cannot be empty");
    }
    if (route.charAt(0) != '/') {
      throw new IllegalArgumentException("route must start with '/'");
    }
  }
}
