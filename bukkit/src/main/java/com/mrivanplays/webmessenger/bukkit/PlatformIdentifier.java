package com.mrivanplays.webmessenger.bukkit;

import com.mrivanplays.webmessenger.api.Platform;

public class PlatformIdentifier {

  private static Platform platform;

  public static Platform get() {
    if (platform != null) {
      return platform;
    }
    try {
      Class.forName("com.destroystokyo.paper.PaperConfig");
      platform = Platform.PAPER_LIKE;
    } catch (ClassNotFoundException e) {
      platform = Platform.BUKKIT_LIKE;
    }
    return platform;
  }
}
