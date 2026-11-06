package com.mrivanplays.webmessenger.core;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.util.Either;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ServerDataProvider extends ServerInfo {

  PlayerInfo playerInfo(String name);

  PlayerInfo playerInfo(UUID uuid);

  CompletableFuture<Boolean> performCommand(String command);

  ServerInfo serverInfo(String serverName);

  Platform type();

  CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie);

  boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data);
}
