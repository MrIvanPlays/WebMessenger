package com.mrivanplays.webmessenger.api;

/**
 * Represents the platform the web messenger server is running on
 *
 * @author Ivan Pekov
 * @since 0.0.1
 */
public enum Platform {
  BUNGEECORD("BungeeCord"),
  BUKKIT_LIKE("Bukkit like"),
  /*
   * We know paper is a BUKKIT_LIKE platform, however the bukkit plugin
   * implementation once uses a API call that only exists on paper (and its forks),
   * hence the redundancy reason
   */
  PAPER_LIKE("Paper like"),
  VELOCITY("Velocity");

  private final String prettyName;

  Platform(String prettyName) {
    this.prettyName = prettyName;
  }

  /**
   * Check whether the given platform is bukkit like
   *
   * @return whether bukkit
   */
  public boolean isBukkitLike() {
    return this == BUKKIT_LIKE || this == PAPER_LIKE;
  }

  /**
   * Check whether the given platform is a proxy
   *
   * @return whether proxy
   */
  public boolean isProxy() {
    return this == BUNGEECORD || this == VELOCITY;
  }

  /**
   * Get a pretty {@link java.lang.String} {@code name} of the platform
   *
   * @return pretty name
   */
  public String prettyName() {
    return this.prettyName;
  }
}
