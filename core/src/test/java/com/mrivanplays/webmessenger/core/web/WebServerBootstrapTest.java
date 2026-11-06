package com.mrivanplays.webmessenger.core.web;

import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.api.event.EventBus;
import com.mrivanplays.webmessenger.api.event.HandleableEvent;
import com.mrivanplays.webmessenger.api.event.bundled.CustomRequestRegisterEvent;
import com.mrivanplays.webmessenger.api.event.bundled.HttpRequestHandleEvent;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import com.mrivanplays.webmessenger.api.request.RequestType;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.log.PluginLogger;
import com.mrivanplays.webmessenger.core.util.Either;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WebServerBootstrapTest {

  void main(String[] args) {
    WebMessengerPlugin plugin =
        new WebMessengerPlugin(
            PluginLogger.fromJavaLogger(Logger.getLogger("test")), Path.of("."), null);
    plugin.setDataProvider(new TestSDPImpl());
    plugin.setAsyncExecutor(Runnable::run);
    plugin.enable();
    this.registerCustomRequest();
    EventBus.INSTANCE.registerListener(
        new Identifier("my", "pluginname"),
        CustomRequestRegisterEvent.class,
        event -> {
          plugin
              .getLogger()
              .info(
                  "'"
                      + event.requestMetadata().identifier().asName()
                      + " ' registered for route '"
                      + event.requestMetadata().route()
                      + "'");
          event.handle(HandleableEvent.Response.unhandled());
        });
    EventBus.INSTANCE.registerListener(
        new Identifier("my", "pluginname"),
        HttpRequestHandleEvent.class,
        event -> {
          plugin.getLogger().info("Handling request: " + event.context().metadata().route());
          event.handle(HandleableEvent.Response.unhandled());
        });
  }

  void registerCustomRequest() {
    // since this is not a unit test file I have the possibility
    // to add a bit of randomness just to be sure that requests register
    // on runtime
    //    int random = new Random().nextInt(1, 100);
    //    System.out.println("random is " + random);
    //    if (random < 50) {
    //      // register now
    //      CustomRequestRegistrar.INSTANCE.registerRequest(new CustomRequestTest());
    //    } else {
    // register after 30 seconds
    try (ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor()) {
      scheduled.schedule(
          () ->
              WebMessengerServer.getInstance()
                  .customRequestRegistrar()
                  .registerRequest(new CustomRequestTest()),
          30,
          TimeUnit.SECONDS);
    }
    // }
  }

  class TestSDPImpl implements ServerDataProvider {

    @Override
    public PlayerInfo playerInfo(String name) {
      return new PlayerInfo(name, UUID.randomUUID(), null);
    }

    @Override
    public PlayerInfo playerInfo(UUID uuid) {
      return null;
    }

    @Override
    public CompletableFuture<Boolean> performCommand(String command) {
      System.out.println("performing cmd: " + command);
      return CompletableFuture.completedFuture(true);
    }

    @Override
    public ServerInfo serverInfo(String serverName) {
      return new ServerInfo() {
        @Override
        public int onlinePlayers() {
          return 10;
        }

        @Override
        public Collection<PlayerInfo> connectedPlayers() {
          return List.of();
        }

        @Override
        public String name() {
          return serverName;
        }

        @Override
        public CompletableFuture<ServerPing> ping() {
          return CompletableFuture.completedFuture(new ServerPing("26.3", true, "MOTD"));
        }
      };
    }

    @Override
    public Platform type() {
      return Platform.BUNGEECORD;
    }

    private Map<String, byte[]> cookies = new HashMap<>();

    @Override
    public CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie) {
      return CompletableFuture.completedFuture(cookies.getOrDefault(cookie, null));
    }

    @Override
    public boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data) {
      cookies.put(cookie, data);
      System.out.println("stored cookie " + new String(data, StandardCharsets.UTF_8));
      return true;
    }

    @Override
    public int onlinePlayers() {
      return 30;
    }

    @Override
    public Collection<PlayerInfo> connectedPlayers() {
      return List.of();
    }

    @Override
    public String name() {
      return "bungeecord";
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
      return CompletableFuture.completedFuture(new ServerPing("26.3", true, "MOTD"));
    }
  }

  class CustomRequestTest implements CustomRequest {

    @Override
    public RequestMetadata metadata() {
      return RequestMetadata.of(new Identifier("my", "pluginname"), "/custom", RequestType.GET);
    }

    @Override
    public void handle(RequestResponseContext ctx) throws IOException {
      ctx.setResponseCode(200);
      JsonObject o = new JsonObject();
      o.addProperty("hello", "my name is anton");
      o.addProperty("every", "morning i get up and go to school");
      ctx.writeResponse(o);
    }
  }
}
