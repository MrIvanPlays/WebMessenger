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

import com.mrivanplays.webmessenger.core.log.PluginLogger;
import org.slf4j.Logger;

public class VelocityPluginLogger implements PluginLogger {

  private final Logger parent;
  private boolean enableDebugMessages = false;

  public VelocityPluginLogger(Logger parent) {
    this.parent = parent;
  }

  @Override
  public void info(String message) {
    this.parent.info(message);
  }

  @Override
  public void warn(String message) {
    this.parent.warn(message);
  }

  @Override
  public void error(String message, Throwable e) {
    this.parent.error(message, e);
  }

  @Override
  public void debug(String message) {
    if (enableDebugMessages) {
      parent.debug(message);
    }
  }

  @Override
  public void enableDebugMessages() {
    parent.debug("Enabled debug messages");
    this.enableDebugMessages = true;
  }
}
