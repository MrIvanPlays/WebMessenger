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

import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.CookieReceiveEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;

public class IsolatedVelocityPluginBootstrap {

  private static IsolatedVelocityPluginBootstrap instance;

  public static IsolatedVelocityPluginBootstrap getInstance() {
    if (instance == null) {
      instance = new IsolatedVelocityPluginBootstrap();
    }
    return instance;
  }

  private IsolatedVelocityPluginBootstrap() {}

  private VelocityDataProvider dataProvider;
  private WebMessengerPlugin plugin;

  public void boot(
      WebMessengerVelocity main, Logger slf4jLogger, ProxyServer server, Path dataDirectory) {
    server.getEventManager().register(main, this);
    plugin = new WebMessengerPlugin(new VelocityPluginLogger(slf4jLogger), dataDirectory);
    dataProvider = new VelocityDataProvider(server);
    plugin.setDataProvider(dataProvider);
    plugin.setAsyncExecutor(r -> server.getScheduler().buildTask(main, r).schedule());
    var cont = server.getPluginManager().getPlugin("webmessenger-velocity");
    cont.ifPresent(pluginContainer ->
        plugin.setPluginVersion(pluginContainer.getDescription().getVersion().orElse("unknown")));
    plugin.enable();
  }

  public void shutdown() {
    plugin.disable();
  }

  @Subscribe
  public void handleCookie(CookieReceiveEvent event) {
    if (!dataProvider.completeCookieRequestFuture(
        event.getOriginalKey(), event.getPlayer().getUniqueId(), event.getOriginalData())) {
      event.setResult(CookieReceiveEvent.ForwardResult.handled());
    }
  }
}
