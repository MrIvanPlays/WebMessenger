package com.mrivanplays.webmessenger.velocity;

import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class VelocityServerInfo implements ServerInfo {
  private RegisteredServer con;

  public VelocityServerInfo(RegisteredServer con) {
    this.con = con;
  }

  @Override
  public int onlinePlayers() {
    return con.getPlayersConnected().size();
  }

  @Override
  public Collection<PlayerInfo> connectedPlayers() {
    return con.getPlayersConnected().isEmpty()
        ? Collections.emptyList()
        : con.getPlayersConnected().stream()
            .map(p -> new PlayerInfo(p.getUsername(), p.getUniqueId(), this))
            .collect(Collectors.toList());
  }

  @Override
  public String name() {
    return con.getServerInfo().getName();
  }

  @Override
  public CompletableFuture<ServerPing> ping() {
    return con.ping()
        .thenApply(vPing -> {
          if (vPing == null) {
            return new ServerPing("unknown", false, "unknown");
          }
          return new ServerPing(
              vPing.getVersion().getName(),
              true,
              vPing.getDescriptionComponent() == null
                  ? "unknown"
                  : PlainTextComponentSerializer.plainText()
                      .serialize(vPing.getDescriptionComponent()));
        });
  }
}
