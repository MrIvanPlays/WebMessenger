package com.mrivanplays.webmessenger.velocity;

import com.google.inject.Inject;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.CookieReceiveEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;

public class WebMessengerVelocity {
  private VelocityDataProvider dataProvider;
  private WebMessengerPlugin plugin;
  private ProxyServer server;

  @Inject
  public WebMessengerVelocity(Logger logger, ProxyServer server, Path dataDirectory) {
    plugin = new WebMessengerPlugin(new VelocityPluginLogger(logger), dataDirectory);
    this.server = server;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    dataProvider = new VelocityDataProvider(server);
    plugin.setDataProvider(dataProvider);
    plugin.setAsyncExecutor(r -> server.getScheduler().buildTask(this, r));
    var cont = server.getPluginManager().getPlugin("webmessenger-velocity");
    cont.ifPresent(pluginContainer ->
        plugin.setPluginVersion(pluginContainer.getDescription().getVersion().orElse("unknown")));
    plugin.enable();
  }

  @Subscribe
  public void handleCookie(CookieReceiveEvent event) {
    if (!dataProvider.completeCookieRequestFuture(
        event.getOriginalKey(), event.getPlayer().getUniqueId(), event.getOriginalData())) {
      event.setResult(CookieReceiveEvent.ForwardResult.handled());
    }
  }

  @Subscribe
  public void onShutdown(ProxyShutdownEvent event) {
    plugin.disable();
  }
}
