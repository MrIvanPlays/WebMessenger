package com.mrivanplays.webmessenger.bungee;

import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.log.PluginLogger;
import net.md_5.bungee.api.plugin.Plugin;

public final class WebMessengerBungee extends Plugin {

  private WebMessengerPlugin plugin;

  @Override
  public void onEnable() {
    plugin =
        new WebMessengerPlugin(
            PluginLogger.fromJavaLogger(getLogger()),
            getDataFolder().toPath(),
            getDescription().getVersion());
    plugin.setDataProvider(new BungeeDataProvider());
    plugin.setAsyncExecutor((r) -> getProxy().getScheduler().runAsync(this, r));
    plugin.enable();
  }

  @Override
  public void onDisable() {
    plugin.disable();
  }
}
