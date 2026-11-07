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
