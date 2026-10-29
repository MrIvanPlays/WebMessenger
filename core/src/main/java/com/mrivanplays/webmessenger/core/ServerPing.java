package com.mrivanplays.webmessenger.core;

public record ServerPing(String version, boolean online, String motd) {}
