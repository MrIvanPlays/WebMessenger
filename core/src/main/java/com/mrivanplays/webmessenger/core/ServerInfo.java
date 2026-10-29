package com.mrivanplays.webmessenger.core;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface ServerInfo {

  int onlinePlayers();

  Collection<PlayerInfo> connectedPlayers();

  String name();

  CompletableFuture<ServerPing> ping();
}
