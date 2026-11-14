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
package com.mrivanplays.webmessenger.core;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.core.config.Config;
import com.mrivanplays.webmessenger.core.log.PluginLogger;
import com.mrivanplays.webmessenger.core.web.WebServerBootstrap;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public final class WebMessengerPlugin {

  public static final Identifier API_IDENTIFIER = new Identifier("webmessenger", "plugin");

  private ServerDataProvider dataProvider;
  private PluginLogger logger;
  private Executor asyncExecutor;
  private Config config;
  private Path dataDirectory;
  private String pluginVersion;

  private WebServerBootstrap webBootstrap;

  public WebMessengerPlugin(PluginLogger logger, Path dataDirectory) {
    this(logger, dataDirectory, "unknown");
  }

  public WebMessengerPlugin(PluginLogger logger, Path dataDirectory, String pluginVersion) {
    this.logger = logger;
    this.dataDirectory = dataDirectory;
    this.pluginVersion = pluginVersion;
  }

  public void enable() {
    this.config = new Config(new File(this.dataDirectory.toFile(), "config.yml"), logger);
    webBootstrap = new WebServerBootstrap(this);
    WebMessengerServer.setInstance(webBootstrap);
    webBootstrap.boot();
  }

  public void disable() {
    if (webBootstrap != null) {
      webBootstrap.shutdown();
    }
  }

  public ServerDataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(ServerDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public PluginLogger getLogger() {
    return logger;
  }

  public Executor getAsyncExecutor() {
    return asyncExecutor;
  }

  public void setAsyncExecutor(Executor async) {
    this.asyncExecutor = async;
  }

  public Config getConfig() {
    return config;
  }

  public String getPluginVersion() {
    return pluginVersion;
  }

  public void setPluginVersion(String pluginVersion) {
    this.pluginVersion = pluginVersion;
  }
}
