package com.mrivanplays.webmessenger.bukkit.command;

import com.mrivanplays.webmessenger.api.Platform;
import com.mrivanplays.webmessenger.bukkit.PlatformIdentifier;
import com.mrivanplays.webmessenger.bukkit.WebMessengerBukkit;

public class PerformCommandResolver {

  private final PerformCommand instance;

  public PerformCommandResolver(WebMessengerBukkit plugin) {
    this.instance =
        PlatformIdentifier.get() == Platform.PAPER_LIKE
            ? new PerformCommandPaper(plugin)
            : new PerformCommandBukkit();
  }

  public PerformCommand get() {
    return instance;
  }
}
