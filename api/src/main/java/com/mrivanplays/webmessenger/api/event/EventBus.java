package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.webmessenger.api.Identifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Represents a event bus, which is responsible for handling event listeners. */
public enum EventBus {
  INSTANCE;

  private final Lock listenersLock = new ReentrantLock();
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
      SortedSet<EventListenerIdentified> eventListeners = this.listeners.remove(eventClass);
      if (eventListeners == null) {
        eventListeners =
            new ConcurrentSkipListSet<>(Comparator.comparing(l -> l.listener().priority()));
      }
      eventListeners.add(new EventListenerIdentified(listener, identifier));
      this.listeners.put(eventClass, eventListeners);
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
      SortedSet<EventListenerIdentified> eventListeners = this.listeners.remove(eventClass);
      if (eventListeners == null) {
        return;
      }
      eventListeners.removeIf(eventListenerIdentified ->
          eventListenerIdentified.identifier().asName().equalsIgnoreCase(identifier.asName()));
      if (eventListeners.isEmpty()) {
        return;
      }
      this.listeners.put(eventClass, eventListeners);
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
    List<Map.Entry<Class<? extends Event>, Set<EventListenerIdentified>>> toRemove =
        new ArrayList<>();
    listenersLock.lock();
    try {
      for (Map.Entry<Class<? extends Event>, SortedSet<EventListenerIdentified>> entry :
          this.listeners.entrySet()) {
        Set<EventListenerIdentified> listToRemove =
            entry.getValue().stream()
                .filter(l -> l.identifier().asName().equalsIgnoreCase(identifier.asName()))
                .collect(Collectors.toSet());
        toRemove.add(new AbstractMap.SimpleEntry<>(entry.getKey(), listToRemove));
      }
      for (Map.Entry<Class<? extends Event>, Set<EventListenerIdentified>> entryRemove : toRemove) {
        SortedSet<EventListenerIdentified> current = this.listeners.remove(entryRemove.getKey());
        current.removeAll(entryRemove.getValue());
        if (current.isEmpty()) {
          continue;
        }
        this.listeners.put(entryRemove.getKey(), current);
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
    if (this.listeners.get(event.getClass()) == null) {
      return event;
    }
    for (EventListenerIdentified<T> listener : this.listeners.get(event.getClass())) {
      listener.listener().handle(event);
    }
    return event;
  }
}
