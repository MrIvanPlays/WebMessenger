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
package com.mrivanplays.webmessenger.api.request;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

/** Represents a registrar for {@link CustomRequest custom requests} */
@NullMarked
public interface CustomRequestRegistrar {

  /**
   * Register a new custom request
   *
   * @param request request to register
   * @throws IllegalArgumentException if the route given in the {@link RequestMetadata metadata} is
   *     already registered
   */
  void registerRequest(CustomRequest request);

  /**
   * Unregister a custom request that is already registered.
   *
   * @param metadata request metadata
   */
  void unregisterRequest(RequestMetadata metadata);

  /**
   * Check if a route is available
   *
   * @param route route to check
   * @return true if available, false otherwise
   */
  boolean isRouteAvailable(String route);

  /**
   * Get the {@link RequestMetadata request metadata} of the provided route, if such {@link
   * CustomRequest custom request} is registered.
   *
   * @param route route to check
   * @return populated if present, empty if null
   */
  Optional<RequestMetadata> metadataOf(String route);
}
