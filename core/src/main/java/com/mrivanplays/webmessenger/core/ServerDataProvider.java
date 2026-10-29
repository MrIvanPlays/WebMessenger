package com.mrivanplays.webmessenger.core;

import com.mrivanplays.webmessenger.api.Platform;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ServerDataProvider extends ServerInfo {

  PlayerInfo playerInfo(String name);

  PlayerInfo playerInfo(UUID uuid);

  CompletableFuture<Boolean> performCommand(String command);

  ServerInfo serverInfo(String serverName);

  Platform type();
}
