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
