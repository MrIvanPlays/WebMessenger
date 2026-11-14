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
