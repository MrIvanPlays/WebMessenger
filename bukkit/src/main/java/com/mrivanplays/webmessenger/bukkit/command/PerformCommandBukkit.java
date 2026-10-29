package com.mrivanplays.webmessenger.bukkit.command;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;

public class PerformCommandBukkit implements PerformCommand {
  @Override
  public CompletableFuture<Boolean> performCommand(String command) {
    return CompletableFuture.completedFuture(
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
  }
}
