package com.mrivanplays.webmessenger.bukkit.command;

import com.mrivanplays.webmessenger.bukkit.WebMessengerBukkit;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;

public class PerformCommandPaper implements PerformCommand {

  private final WebMessengerBukkit plugin;

  public PerformCommandPaper(WebMessengerBukkit plugin) {
    this.plugin = plugin;
  }

  @Override
  public CompletableFuture<Boolean> performCommand(String command) {
    return CompletableFuture.supplyAsync(
        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command),
        Bukkit.getScheduler().getMainThreadExecutor(plugin));
  }
}
