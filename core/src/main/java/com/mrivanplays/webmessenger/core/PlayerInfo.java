package com.mrivanplays.webmessenger.core;

import java.util.UUID;

public record PlayerInfo(String name, UUID uuid, ServerInfo connectedServerInfo) {}
