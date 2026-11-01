package com.mrivanplays.webmessenger.api.request;

import com.google.gson.JsonElement;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

// todo: requires a bunch of documentation in order to guarantee proper API use
public interface RequestResponseContext {

  void writeResponse(@NotNull JsonElement response) throws IOException;

  Optional<String> attribute(@NotNull String name);

  void setResponseCode(int httpCode);

  Optional<InputStream> requestBody();

  Optional<CustomHeader> requestHeader(@NotNull String key);

  void addResponseHeader(@NotNull CustomHeader header);

  boolean isDone();

  @NotNull
  InetSocketAddress remoteAddress();

  @NotNull
  RequestMetadata metadata();
}
