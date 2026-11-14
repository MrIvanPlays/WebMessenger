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
package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.events.ResultedEvent;
import com.mrivanplays.webmessenger.api.request.RequestResponseContext;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;

/** Event, called when a HTTP request is to be handled by the WebMessenger web server */
@NullMarked
public class HttpRequestHandleEvent implements ResultedEvent<ResultedEvent.GenericResult> {
  private final RequestResponseContext ctx;
  private GenericResult result;

  public HttpRequestHandleEvent(RequestResponseContext ctx) {
    this.ctx = Objects.requireNonNull(ctx, "context");
    this.result = GenericResult.ALLOWED;
  }

  /** {@inheritDoc} */
  @Override
  public GenericResult result() {
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public void setResult(ResultedEvent.GenericResult newResult) {
    this.result = Objects.requireNonNull(newResult, "newResult");
  }

  /**
   * Get the {@link RequestResponseContext response context} for this request.
   *
   * @return context
   */
  public RequestResponseContext context() {
    return ctx;
  }
}
