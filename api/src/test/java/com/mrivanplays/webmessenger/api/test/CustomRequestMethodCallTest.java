package com.mrivanplays.webmessenger.api.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CustomRequestMethodCallTest {

  record DummyResponseContext(RequestMetadata metadata) implements RequestResponseContext {

    @Override
    public void writeResponse(JsonElement response) throws IOException {
      Assertions.fail();
    }

    @Override
    public Optional<String> attribute(String name) {
      return Optional.empty();
    }

    @Override
    public void setResponseCode(int httpCode) {}

    @Override
    public Optional<InputStream> requestBody() {
      return Optional.empty();
    }

    @Override
    public Optional<CustomHeader> requestHeader(String key) {
      return Optional.empty();
    }

    @Override
    public void addResponseHeader(CustomHeader header) {}

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public InetSocketAddress remoteAddress() {
      return null;
    }
  }

  @BeforeAll
  static void setup() {
    CustomRequestRegistrar.INSTANCE.registerRequest(
        CustomRequest.functional(
            RequestMetadata.simpleGet(CommonValues.TEST_IDENTIFIER, "/test"),
            ctx -> ctx.writeResponse(new JsonPrimitive("hello"))));
  }

  @Test
  void testMethodHandleCall() {
    CustomRequest req = CustomRequestRegistrar.INSTANCE.getRegistrar().getFirst();
    try {
      req.handle(new DummyResponseContext(req.metadata()));
    } catch (IOException e) {
      Assertions.fail(e);
    }
  }
}
