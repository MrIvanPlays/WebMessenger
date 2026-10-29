package com.mrivanplays.webmessenger.core.web;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.api.event.EventBus;
import com.mrivanplays.webmessenger.api.event.bundled.CustomRequestRegisterEvent;
import com.mrivanplays.webmessenger.api.event.bundled.WebServerPostStartupEvent;
import com.mrivanplays.webmessenger.api.event.bundled.WebServerPreStartupEvent;
import com.mrivanplays.webmessenger.api.event.bundled.WebServerShutdownEvent;
import com.mrivanplays.webmessenger.api.ratelimit.RateLimiter;
import com.mrivanplays.webmessenger.api.ratelimit.SimpleRateLimiter;
import com.mrivanplays.webmessenger.api.request.CustomRequest;
import com.mrivanplays.webmessenger.api.request.CustomRequestRegistrar;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.web.generic.NotFoundRequest;
import com.mrivanplays.webmessenger.core.web.generic.RobotsTxtEndpoint;
import com.mrivanplays.webmessenger.core.web.generic.AvailableRequestsRequest;
import com.mrivanplays.webmessenger.core.web.minecraft.PerformCommandRequest;
import com.mrivanplays.webmessenger.core.web.minecraft.PlayerInfoRequest;
import com.mrivanplays.webmessenger.core.web.minecraft.ServerInfoRequest;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebServerBootstrap extends WebMessengerServer {

  private final WebMessengerPlugin plugin;
  private HttpServer server;
  private Map<String, MessengerHttpHandler> DEFAULT_HANDLERS = new HashMap<>();
  private RateLimiter rateLimiter;

  public WebServerBootstrap(WebMessengerPlugin plugin) {
    this.plugin = plugin;
    this.DEFAULT_HANDLERS.put("/playerInfo", new PlayerInfoRequest(plugin));
    this.DEFAULT_HANDLERS.put("/serverInfo", new ServerInfoRequest(plugin));
    this.DEFAULT_HANDLERS.put("/executeCommand", new PerformCommandRequest(plugin));
    this.DEFAULT_HANDLERS.put("/", new NotFoundRequest(plugin));
    this.DEFAULT_HANDLERS.put("/robots.txt", new RobotsTxtEndpoint(plugin));
    AvailableRequestsRequest arr = new AvailableRequestsRequest(plugin);
    arr.populateDefaults(this.DEFAULT_HANDLERS);
    this.DEFAULT_HANDLERS.put("/availableRequests", arr);
  }

  public void boot() {
    EventBus.INSTANCE.fire(new WebServerPreStartupEvent());
    if (plugin.getConfig().isRateLimitEnabled()) {
      if (rateLimiter == null) {
        this.setRateLimiter(
            SimpleRateLimiter.of(
                RateLimiter.Settings.of(
                    plugin.getConfig().getRateLimitDuration(), plugin.getConfig().getRateLimit())));
      }
    }
    try {
      server = HttpServer.create(new InetSocketAddress(plugin.getConfig().getPort()), 0);

      for (Map.Entry<String, MessengerHttpHandler> handlerEntry : DEFAULT_HANDLERS.entrySet()) {
        plugin.getLogger().debug("Registering handler " + handlerEntry.getKey());
        server.createContext(handlerEntry.getKey(), handlerEntry.getValue());
      }

      for (CustomRequest entry : CustomRequestRegistrar.INSTANCE.getRegistrar()) {
        if (DEFAULT_HANDLERS.keySet().stream()
            .anyMatch(s -> s.equalsIgnoreCase(entry.metadata().route()))) {
          plugin
              .getLogger()
              .warn(
                  "Identifier '"
                      + entry.metadata().identifier().asName()
                      + "' tried to register a custom route for '"
                      + entry.metadata().route()
                      + "' and will be ignored.");
          continue;
        }
        CustomRequestRegisterEvent event =
            EventBus.INSTANCE.fire(new CustomRequestRegisterEvent(entry.metadata()));
        if (event.response().isHandled()) {
          continue;
        }
        plugin
            .getLogger()
            .debug(
                "Registering handler '"
                    + entry.metadata().route()
                    + "' "
                    + "with identifier '"
                    + entry.metadata().identifier().asName());
        server.createContext(
            entry.metadata().route(), new CustomRequestRegistration(this.plugin, entry));
        plugin
            .getLogger()
            .info(
                "Identifier '"
                    + entry.metadata().identifier().asName()
                    + "' registered route '"
                    + entry.metadata().route()
                    + "'");
      }

      server.setExecutor(plugin.getAsyncExecutor());
      server.start();
      plugin.getLogger().info("HTTP server running at port: " + plugin.getConfig().getPort());
      plugin
          .getLogger()
          .info(
              "Running on "
                  + plugin.getDataProvider().type().prettyName()
                  + " minecraft server software");
      EventBus.INSTANCE.fire(new WebServerPostStartupEvent());
    } catch (IOException e) {
      plugin.getLogger().error("Couldn't start server", e);
    }
  }

  public void shutdown() {
    if (server != null) {
      plugin.getLogger().info("Waiting on HTTP server to shutdown");
      server.stop(2);
      plugin.getLogger().info("HTTP server shutdown success");
      EventBus.INSTANCE.fire(new WebServerShutdownEvent());
    }
  }

  @Override
  public Optional<RateLimiter> rateLimiter() {
    return Optional.ofNullable(rateLimiter);
  }

  @Override
  public void setRateLimiter(RateLimiter rateLimiter) {
    plugin.getLogger().debug("Rate limiter changed");
    this.rateLimiter = rateLimiter;
    // update for all handlers
    for (MessengerHttpHandler handler : DEFAULT_HANDLERS.values()) {
      handler.setRateLimiter(rateLimiter);
    }
  }

  @Override
  public String version() {
    return plugin.getPluginVersion();
  }

  @Override
  public Platform platform() {
    return plugin.getDataProvider().type();
  }
}
