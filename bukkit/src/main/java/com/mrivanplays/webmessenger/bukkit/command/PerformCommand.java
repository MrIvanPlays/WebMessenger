package com.mrivanplays.webmessenger.bukkit.command;

import java.util.concurrent.CompletableFuture;

public interface PerformCommand {

  CompletableFuture<Boolean> performCommand(String command);
}
