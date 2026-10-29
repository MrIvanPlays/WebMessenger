package com.mrivanplays.webmessenger.api.event;

/**
 * Represents an enum of priorities, used to sort {@link EventListener event listeners} in call
 * priority when {@link EventBus#fire} is called
 */
public enum EventPriority {
  /** A {@link EventListener event listener} will be one of the first to get called */
  FIRST,
  NORMAL,
  /** A {@link EventListener event listener} will be one of the last to get called */
  LAST
}
