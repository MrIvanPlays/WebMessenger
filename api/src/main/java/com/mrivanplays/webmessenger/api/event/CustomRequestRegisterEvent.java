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
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;

/**
 * Event, called when a {@link com.mrivanplays.webmessenger.api.request.CustomRequest custom
 * request} is registered to the WebMessenger web server
 */
@NullMarked
public class CustomRequestRegisterEvent implements ResultedEvent<ResultedEvent.GenericResult> {

  private GenericResult result;
  private final RequestMetadata requestMetadata;

  public CustomRequestRegisterEvent(RequestMetadata requestMetadata) {
    this.result = GenericResult.ALLOWED;
    this.requestMetadata = requestMetadata;
  }

  @Override
  public GenericResult result() {
    return result;
  }

  @Override
  public void setResult(ResultedEvent.GenericResult newResult) {
    this.result = Objects.requireNonNull(newResult, "newResult");
  }

  /**
   * Get the {@link RequestMetadata request metadata} which the request is registered with.
   *
   * @return metadata
   */
  public RequestMetadata requestMetadata() {
    return this.requestMetadata;
  }
}
