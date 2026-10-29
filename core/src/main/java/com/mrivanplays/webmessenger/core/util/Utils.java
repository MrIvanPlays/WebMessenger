package com.mrivanplays.webmessenger.core.util;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

  public static boolean listEquals(List<String> first, List<String> second) {
    if (first.size() != second.size()) {
      return false;
    }
    boolean ret = true;
    for (int i = 0; i < first.size(); i++) {
      String valOne = first.get(i);
      String valTwo = second.get(i);
      if (!valOne.equals(valTwo)) {
        ret = false;
        break;
      }
    }
    return ret;
  }
}
