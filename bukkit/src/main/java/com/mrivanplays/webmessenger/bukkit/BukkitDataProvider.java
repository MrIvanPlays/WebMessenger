/*
 * Copyright (c) Ivan Pekov 2026
 * Copyright (c) Contributors 2026
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mrivanplays.webmessenger.bukkit;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.mrivanplays.webmessenger.core.util.Either;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class BukkitDataProvider implements ServerDataProvider {

  private final ServerPing ping;
  private final WebMessengerBukkit plugin;

  public BukkitDataProvider(WebMessengerBukkit plugin) {
    this.ping = new ServerPing(Bukkit.getMinecraftVersion(), true, Bukkit.getMotd());
    this.plugin = plugin;
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
    return CompletableFuture.supplyAsync(
        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command),
        t -> Bukkit.getScheduler().runTask(plugin, t));
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
  public CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie) {
    var player =
        info.type().isLeft()
            ? Bukkit.getPlayer(info.left().get())
            : Bukkit.getPlayer(info.right().get());
    if (player == null) {
      return CompletableFuture.completedFuture(null);
    }
    var spl = cookie.split(":");
    return player.retrieveCookie(new NamespacedKey(spl[0], spl[1]));
  }

  @Override
  public boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data) {
    var player =
        info.type().isLeft()
            ? Bukkit.getPlayer(info.left().get())
            : Bukkit.getPlayer(info.right().get());
    if (player == null) {
      return false;
    }
    var spl = cookie.split(":");
    player.storeCookie(new NamespacedKey(spl[0], spl[1]), data);
    return true;
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
