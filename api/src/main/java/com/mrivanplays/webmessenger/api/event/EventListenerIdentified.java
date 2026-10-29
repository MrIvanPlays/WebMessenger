package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.webmessenger.api.Identifier;

record EventListenerIdentified<T>(EventListener<T> listener, Identifier identifier) {}
