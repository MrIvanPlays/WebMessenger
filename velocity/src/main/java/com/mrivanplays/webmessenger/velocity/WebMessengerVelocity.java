package com.mrivanplays.webmessenger.velocity;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.mrivanplays.webmessenger.dependency.DependencyDownloader;
import com.mrivanplays.webmessenger.dependency.DependencyResolver;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;

public class WebMessengerVelocity {
  private final Logger slf4jLogger;
  private final ProxyServer server;
  private final Path dataDirectory;

  @Inject
  public WebMessengerVelocity(
      Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
    this.slf4jLogger = logger;
    this.dataDirectory = dataDirectory;
    this.server = server;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    if (!Files.exists(dataDirectory)) {
      try {
        Files.createDirectory(dataDirectory);
      } catch (IOException e) {
        slf4jLogger.error("Failed to create directory: " + dataDirectory, e);
      }
    }
    var property =
        System.getProperty("com.mrivanplays.repository") == null
            ? System.getenv().get("com.mrivanplays.repoistory")
            : System.getProperty("com.mrivanplays.repository");
    // load dependencies BEFORE doing anything else
    // a hacky way not to import anything from downloaded libraries
    DependencyDownloader.resolveDependencies(
        DependencyResolver.resolveDependencies(
            new Gson(), getClass().getClassLoader().getResourceAsStream("dependencies.json")),
        property == null
            ? "https://maven-central.storage-download.googleapis.com/maven2/"
            : property,
        dataDirectory.resolve("dependencies"),
        o -> {
          if (o instanceof Throwable e) {
            slf4jLogger.error("Exception resolving dependencies", e);
          } else {
            slf4jLogger.info(String.valueOf(o));
          }
        },
        p -> server.getPluginManager().addToClasspath(this, p));
    com.mrivanplays.webmessenger.velocity.IsolatedVelocityPluginBootstrap.getInstance()
        .boot(this, slf4jLogger, server, dataDirectory);
  }

  @Subscribe
  public void onShutdown(ProxyShutdownEvent event) {
    com.mrivanplays.webmessenger.velocity.IsolatedVelocityPluginBootstrap.getInstance().shutdown();
  }
}
