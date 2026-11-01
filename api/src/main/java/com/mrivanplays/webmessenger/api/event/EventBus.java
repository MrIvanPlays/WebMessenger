package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.webmessenger.api.Identifier;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/** Represents a event bus, which is responsible for handling event listeners. */
public enum EventBus {
  INSTANCE;

  private final Lock listenersLock = new ReentrantLock();
  private final AtomicLong registrationSequence = new AtomicLong();
  private final Map<Class<? extends Event>, SortedSet<EventListenerIdentified>> listeners =
      new ConcurrentHashMap<>();

  /**
   * Register a new listener with a normal priority.
   *
   * @param identifier identifier registrar
   * @param eventClass event class
   * @param listener listener handler
   * @param <T> event
   */
  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, Consumer<T> listener) {
    this.registerListener(identifier, eventClass, EventPriority.NORMAL, listener);
  }

  /**
   * Register a new listener.
   *
   * @param identifier identifier registrar
   * @param eventClass event class
   * @param priority in which priority shall this listener be called
   * @param listener listener handler
   * @param <T> event
   */
  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, EventPriority priority, Consumer<T> listener) {
    this.registerListener(identifier, eventClass, EventListener.of(priority, listener));
  }

  /**
   * Register a new listener.
   *
   * @param identifier identifier registrar
   * @param eventClass event class
   * @param listener {@link EventListener event listener}
   * @param <T> event
   */
  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, EventListener<T> listener) {
    listenersLock.lock();
    try {
      SortedSet<EventListenerIdentified> eventListeners =
          this.listeners.computeIfAbsent(
              eventClass,
              _ ->
                  new ConcurrentSkipListSet<>(
                      Comparator.comparing((EventListenerIdentified l) -> l.listener().priority())
                          .thenComparingLong(EventListenerIdentified::registrationSequence)));
      eventListeners.add(
          new EventListenerIdentified<>(
              listener, identifier, registrationSequence.getAndIncrement()));
    } finally {
      listenersLock.unlock();
    }
  }

  /**
   * Unregisters a listener for the specified {@code eventClass}, registered by the specified {@link
   * Identifier identifier} registrator, if there is such
   *
   * @param identifier registrator identifier
   * @param eventClass event class
   * @param <T> event
   */
  public <T extends Event> void unregisterListener(Identifier identifier, Class<T> eventClass) {
    listenersLock.lock();
    try {
      SortedSet<EventListenerIdentified> eventListeners = this.listeners.get(eventClass);
      if (eventListeners == null) {
        return;
      }
      eventListeners.removeIf(eventListenerIdentified ->
          eventListenerIdentified.identifier().asName().equalsIgnoreCase(identifier.asName()));
    } finally {
      listenersLock.unlock();
    }
  }

  /**
   * Unregisters all listeners, registered by the specified {@link Identifier identifier}
   *
   * @param identifier identifier registrator
   */
  public void unregisterListeners(Identifier identifier) {
    listenersLock.lock();
    try {
      for (Set<EventListenerIdentified> eventListeners : this.listeners.values()) {
        eventListeners.removeIf(
            listener -> listener.identifier().asName().equalsIgnoreCase(identifier.asName()));
      }
    } finally {
      listenersLock.unlock();
    }
  }

  /**
   * Calls all the listeners for the inputted {@code event}
   *
   * @param event event to call
   * @return event instance after handled
   * @param <T> event
   */
  public <T extends Event> T fire(T event) {
    SortedSet<EventListenerIdentified> eventListeners = this.listeners.get(event.getClass());
    if (eventListeners == null) {
      return event;
    }
    for (EventListenerIdentified<T> listener : eventListeners) {
      listener.listener().handle(event);
    }
    return event;
  }
}
