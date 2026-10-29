package com.mrivanplays.webmessenger.velocity;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class VelocityDataProvider implements ServerDataProvider {

  private final ProxyServer server;
  private final ServerPing ping;

  public VelocityDataProvider(ProxyServer server) {
    this.server = server;
    this.ping =
        new ServerPing(
            server.getVersion().getVersion(),
            true,
            PlainTextComponentSerializer.plainText()
                .serialize(server.getConfiguration().getMotd()));
  }

  @Override
  public PlayerInfo playerInfo(String name) {
    return server
        .getPlayer(name)
        .map(p -> new PlayerInfo(p.getUsername(), p.getUniqueId(), constructServerInfo(p)))
        .orElse(null);
  }

  @Override
  public PlayerInfo playerInfo(UUID uuid) {
    return server
        .getPlayer(uuid)
        .map(p -> new PlayerInfo(p.getUsername(), p.getUniqueId(), constructServerInfo(p)))
        .orElse(null);
  }

  @Override
  public CompletableFuture<Boolean> performCommand(String command) {
    return server.getCommandManager().executeAsync(server.getConsoleCommandSource(), command);
  }

  @Override
  public ServerInfo serverInfo(String serverName) {
    return server.getServer(serverName).map(VelocityServerInfo::new).orElse(null);
  }

  @Override
  public Platform type() {
    return Platform.VELOCITY;
  }

  @Override
  public int onlinePlayers() {
    return server.getPlayerCount();
  }

  @Override
  public Collection<PlayerInfo> connectedPlayers() {
    return server.getAllPlayers().stream()
        .map(p -> new PlayerInfo(p.getUsername(), p.getUniqueId(), constructServerInfo(p)))
        .collect(Collectors.toList());
  }

  private ServerInfo constructServerInfo(Player p) {
    ServerConnection con = p.getCurrentServer().orElse(null);
    if (con == null) {
      return null;
    }
    return new VelocityServerInfo(con.getServer());
  }

  @Override
  public String name() {
    return "Velocity";
  }

  @Override
  public CompletableFuture<ServerPing> ping() {
    return CompletableFuture.completedFuture(ping);
  }
}
