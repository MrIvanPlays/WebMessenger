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
package com.mrivanplays.webmessenger.velocity;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.mrivanplays.webmessenger.core.util.Either;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class VelocityDataProvider implements ServerDataProvider {

  private final ProxyServer server;
  private final ServerPing ping;
  private final Map<UUID, Map<Key, CompletableFuture<byte[]>>> requestedCookiesFutures =
      new ConcurrentHashMap<>();

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
  public CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie) {
    var player =
        info.type().isLeft()
            ? server.getPlayer(info.left().get()).orElse(null)
            : server.getPlayer(info.right().get()).orElse(null);
    if (player == null) {
      return CompletableFuture.completedFuture(null);
    }
    var future = new CompletableFuture<byte[]>();
    var spl = cookie.split(":");
    Key key = Key.key(spl[0], spl[1]);
    var cookiesMap =
        requestedCookiesFutures.computeIfAbsent(
            player.getUniqueId(), _ -> new ConcurrentHashMap<>());
    cookiesMap.put(key, future);
    player.requestCookie(key);
    return future;
  }

  // returns whether forward downstream
  public boolean completeCookieRequestFuture(Key key, UUID uniqueId, byte[] info) {
    var map = requestedCookiesFutures.get(uniqueId);
    if (map == null) {
      return true;
    }
    var future = map.remove(key);
    if (future == null) {
      return true;
    }
    future.complete(info);
    return false;
  }

  @Override
  public boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data) {
    var player =
        info.type().isLeft()
            ? server.getPlayer(info.left().get()).orElse(null)
            : server.getPlayer(info.right().get()).orElse(null);
    if (player == null) {
      return false;
    }
    var spl = cookie.split(":");
    player.storeCookie(Key.key(spl[0], spl[1]), data);
    return true;
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
