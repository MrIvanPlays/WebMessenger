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
package com.mrivanplays.webmessenger.bungee;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.PlayerInfo;
import com.mrivanplays.webmessenger.core.ServerDataProvider;
import com.mrivanplays.webmessenger.core.ServerInfo;
import com.mrivanplays.webmessenger.core.ServerPing;
import com.mrivanplays.webmessenger.core.util.Either;
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
  public CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie) {
    var player =
        info.type().isLeft()
            ? ProxyServer.getInstance().getPlayer(info.left().get())
            : ProxyServer.getInstance().getPlayer(info.right().get());
    if (player == null) {
      return CompletableFuture.completedFuture(null);
    }
    return player.retrieveCookie(cookie);
  }

  @Override
  public boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data) {
    var player =
        info.type().isLeft()
            ? ProxyServer.getInstance().getPlayer(info.left().get())
            : ProxyServer.getInstance().getPlayer(info.right().get());
    if (player == null) {
      return false;
    }
    player.storeCookie(cookie, data);
    return true;
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
