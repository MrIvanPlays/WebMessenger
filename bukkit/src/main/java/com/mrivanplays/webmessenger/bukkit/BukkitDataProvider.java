package com.mrivanplays.webmessenger.bukkit;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.bukkit.command.PerformCommand;
import com.mrivanplays.webmessenger.bukkit.command.PerformCommandResolver;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitDataProvider implements ServerDataProvider {

  private final ServerPing ping;
  private final PerformCommand dispatchCommand;

  public BukkitDataProvider(WebMessengerBukkit plugin) {
    this.ping = new ServerPing(Bukkit.getMinecraftVersion(), true, Bukkit.getMotd());
    this.dispatchCommand = new PerformCommandResolver(plugin).get();
  }

  @Override
  public PlayerInfo playerInfo(String name) {
    Player p = Bukkit.getPlayer(name);
    if (p == null) {
      return null;
    }
    return new PlayerInfo(p.getName(), p.getUniqueId(), this);
  }

  @Override
  public PlayerInfo playerInfo(UUID uuid) {
    Player p = Bukkit.getPlayer(uuid);
    if (p == null) {
      return null;
    }
    return new PlayerInfo(p.getName(), p.getUniqueId(), this);
  }

  @Override
  public CompletableFuture<Boolean> performCommand(String command) {
    return dispatchCommand.performCommand(command);
  }

  @Override
  public ServerInfo serverInfo(String serverName) {
    return null;
  }

  @Override
  public Platform type() {
    return PlatformIdentifier.get();
  }

  @Override
  public int onlinePlayers() {
    return Bukkit.getOnlinePlayers().size();
  }

  @Override
  public Collection<PlayerInfo> connectedPlayers() {
    return Bukkit.getOnlinePlayers().stream()
        .map(p -> new PlayerInfo(p.getName(), p.getUniqueId(), this))
        .toList();
  }

  @Override
  public String name() {
    return "Bukkit";
  }

  @Override
  public CompletableFuture<ServerPing> ping() {
    return CompletableFuture.completedFuture(ping);
  }
}
