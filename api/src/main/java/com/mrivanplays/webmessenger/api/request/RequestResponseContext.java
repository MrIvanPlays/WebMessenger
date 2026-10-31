package com.mrivanplays.webmessenger.api.request;

import com.google.gson.JsonElement;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Optional;

// todo: requires a bunch of documentation in order to guarantee proper API use
public interface RequestResponseContext {

  void writeResponse(JsonElement response) throws IOException;

  Optional<String> attribute(String name);

  void setResponseCode(int httpCode);

  Optional<InputStream> requestBody();

  Optional<CustomHeader> requestHeader(String key);

  void addResponseHeader(CustomHeader header);

  boolean isDone();

  InetSocketAddress remoteAddress();

  RequestMetadata metadata();
}
