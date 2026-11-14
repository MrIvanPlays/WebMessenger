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
