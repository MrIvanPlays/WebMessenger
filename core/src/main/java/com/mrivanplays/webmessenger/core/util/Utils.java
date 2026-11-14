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
package com.mrivanplays.webmessenger.core.util;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Utils {

  public static Map<String, String> getAttributes(URI uri) {
    String str = uri.getQuery();
    if (str == null || str.isBlank() || !str.contains("=")) {
      return Collections.emptyMap();
    }
    Map<String, String> ret = new HashMap<>();
    if (str.contains("&")) {
      String[] parts = str.split("&");
      for (String part : parts) {
        String[] kvsplit = part.split("=");
        ret.put(kvsplit[0], kvsplit[1]);
      }
    } else {
      String[] kv = str.split("=");
      ret.put(kv[0], kv[1]);
    }
    return ret;
  }
}
