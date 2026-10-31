package com.mrivanplays.webmessenger.bungee;

import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeServerInfo implements ServerInfo {

  private net.md_5.bungee.api.config.ServerInfo parent;

  public BungeeServerInfo(net.md_5.bungee.api.config.ServerInfo parent) {
    this.parent = parent;
  }

  @Override
  public int onlinePlayers() {
    return parent.getPlayers().size();
  }

  @Override
  public Collection<PlayerInfo> connectedPlayers() {
    return parent.getPlayers().isEmpty()
        ? Collections.emptyList()
        : parent.getPlayers().stream()
            .map(p -> new PlayerInfo(p.getName(), p.getUniqueId(), this))
            .toList();
  }

  @Override
  public String name() {
    return parent.getName();
  }

  @Override
  public CompletableFuture<ServerPing> ping() {
    CompletableFuture<ServerPing> future = new CompletableFuture<>();
    parent.ping((bPing, ex) -> {
      if (ex != null) {
        future.completeExceptionally(ex);
        return;
      }
      if (bPing == null) {
        future.complete(null);
        return;
      }
      future.complete(
          new ServerPing(
              bPing.getVersion().getName(),
              true,
              BaseComponent.toPlainText(bPing.getDescriptionComponent())));
    });
    return future;
  }
}
