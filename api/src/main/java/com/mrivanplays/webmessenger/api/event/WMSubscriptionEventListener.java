package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.events.ListenerIdentifier;
import com.mrivanplays.events.reflection.SubscriptionEventListener;
import com.mrivanplays.webmessenger.api.Identifier;

/**
 * Represents a {@link SubscriptionEventListener} which is modified to work with {@link Identifier}
 * as a {@link ListenerIdentifier}
 */
public interface WMSubscriptionEventListener extends SubscriptionEventListener {

  /**
   * Identifier of all the event listeners
   *
   * @return identifier
   */
  Identifier webMessengerIdentifier();

  @Override
  default boolean matches(ListenerIdentifier other) {
    return webMessengerIdentifier().matches(other);
  }
}
