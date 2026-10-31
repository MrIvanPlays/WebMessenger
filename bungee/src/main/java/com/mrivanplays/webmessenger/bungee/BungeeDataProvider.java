package com.mrivanplays.webmessenger.bungee;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeDataProvider implements ServerDataProvider {

  private final ServerPing ping;

  public BungeeDataProvider() {
    this.ping = new ServerPing(ProxyServer.getInstance().getVersion(), true, "unknown");
  }

  @Override
  public PlayerInfo playerInfo(String name) {
    ProxiedPlayer proxied = ProxyServer.getInstance().getPlayer(name);
    if (proxied == null) {
      return null;
    }
    return new PlayerInfo(
        proxied.getName(),
        proxied.getUniqueId(),
        proxied.getServer() == null ? null : new BungeeServerInfo(proxied.getServer().getInfo()));
  }

  @Override
  public PlayerInfo playerInfo(UUID uuid) {
    ProxiedPlayer proxied = ProxyServer.getInstance().getPlayer(uuid);
    if (proxied == null) {
      return null;
    }
    return new PlayerInfo(
        proxied.getName(),
        proxied.getUniqueId(),
        proxied.getServer() == null ? null : new BungeeServerInfo(proxied.getServer().getInfo()));
  }

  @Override
  public CompletableFuture<Boolean> performCommand(String command) {
    return CompletableFuture.completedFuture(
        ProxyServer.getInstance()
            .getPluginManager()
            .dispatchCommand(ProxyServer.getInstance().getConsole(), command));
  }

  @Override
  public ServerInfo serverInfo(String serverName) {
    net.md_5.bungee.api.config.ServerInfo bungee =
        ProxyServer.getInstance().getServerInfo(serverName);
    if (bungee == null) {
      return null;
    }
    return new BungeeServerInfo(bungee);
  }

  @Override
  public Platform type() {
    return Platform.BUNGEECORD;
  }

  @Override
  public int onlinePlayers() {
    return ProxyServer.getInstance().getOnlineCount();
  }

  @Override
  public Collection<PlayerInfo> connectedPlayers() {
    return ProxyServer.getInstance().getPlayers().stream()
        .map(p ->
            new PlayerInfo(
                p.getName(), p.getUniqueId(), new BungeeServerInfo(p.getServer().getInfo())))
        .toList();
  }

  @Override
  public String name() {
    return "BungeeCord";
  }

  @Override
  public CompletableFuture<ServerPing> ping() {
    return CompletableFuture.completedFuture(ping);
  }
}
