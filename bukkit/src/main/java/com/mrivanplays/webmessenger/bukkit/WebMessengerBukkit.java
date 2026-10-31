package com.mrivanplays.webmessenger.bukkit;

import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.log.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class WebMessengerBukkit extends JavaPlugin {

  private WebMessengerPlugin plugin;

  @Override
  public void onEnable() {
    plugin =
        new WebMessengerPlugin(
            PluginLogger.fromJavaLogger(getLogger()), getDataPath(), getDescription().getVersion());
    plugin.setAsyncExecutor(r -> getServer().getScheduler().runTaskAsynchronously(this, r));
    plugin.setDataProvider(new BukkitDataProvider(this));
    plugin.enable();
  }

  @Override
  public void onDisable() {
    plugin.disable();
  }
}
