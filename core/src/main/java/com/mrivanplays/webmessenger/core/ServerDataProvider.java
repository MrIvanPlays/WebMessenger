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
package com.mrivanplays.webmessenger.core;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.core.util.Either;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ServerDataProvider extends ServerInfo {

  PlayerInfo playerInfo(String name);

  PlayerInfo playerInfo(UUID uuid);

  CompletableFuture<Boolean> performCommand(String command);

  ServerInfo serverInfo(String serverName);

  Platform type();

  CompletableFuture<byte[]> retrieveCookieFor(Either<String, UUID> info, String cookie);

  boolean storeCookie(Either<String, UUID> info, String cookie, byte[] data);
}
