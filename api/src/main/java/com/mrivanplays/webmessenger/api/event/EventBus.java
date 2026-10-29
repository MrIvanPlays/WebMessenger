package com.mrivanplays.webmessenger.api.event;

import com.mrivanplays.webmessenger.api.Identifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// todo: javadoc
public enum EventBus {
  INSTANCE;

  private Map<Class<? extends Event>, List<EventListenerIdentified>> listeners = new HashMap<>();

  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, Consumer<T> listener) {
    this.registerListener(identifier, eventClass, EventPriority.NORMAL, listener);
  }

  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, EventPriority priority, Consumer<T> listener) {
    this.registerListener(identifier, eventClass, EventListener.of(priority, listener));
  }

  public <T extends Event> void registerListener(
      Identifier identifier, Class<T> eventClass, EventListener<T> listener) {
    List<EventListenerIdentified> eventListeners = this.listeners.remove(eventClass);
    if (eventListeners == null) {
      eventListeners = new ArrayList<>();
    }
    eventListeners.add(new EventListenerIdentified(listener, identifier));
    sortList(eventListeners);
    this.listeners.put(eventClass, eventListeners);
  }

  private void sortList(List<EventListenerIdentified> list) {
    list.sort(
        Comparator.comparing(
            eventListenerIdentified -> eventListenerIdentified.listener().priority()));
  }

  public <T extends Event> void unregisterListener(Identifier identifier, Class<T> eventClass) {
    List<EventListenerIdentified> eventListeners = this.listeners.remove(eventClass);
    if (eventListeners == null) {
      return;
    }
    eventListeners.stream()
        .filter(l -> l.identifier().asName().equalsIgnoreCase(identifier.asName()))
        .forEach(eventListeners::remove);
    if (eventListeners.isEmpty()) {
      return;
    }
    sortList(eventListeners);
    this.listeners.put(eventClass, eventListeners);
  }

  public void unregisterListeners(Identifier identifier) {
    List<Map.Entry<Class<? extends Event>, List<EventListenerIdentified>>> toRemove =
        new ArrayList<>();
    for (Map.Entry<Class<? extends Event>, List<EventListenerIdentified>> entry :
        this.listeners.entrySet()) {
      List<EventListenerIdentified> listToRemove =
          entry.getValue().stream()
              .filter(l -> l.identifier().asName().equalsIgnoreCase(identifier.asName()))
              .toList();
      toRemove.add(new AbstractMap.SimpleEntry<>(entry.getKey(), listToRemove));
    }
    for (Map.Entry<Class<? extends Event>, List<EventListenerIdentified>> entryRemove : toRemove) {
      List<EventListenerIdentified> current = this.listeners.remove(entryRemove.getKey());
      current.removeAll(entryRemove.getValue());
      if (current.isEmpty()) {
        continue;
      }
      sortList(current);
      this.listeners.put(entryRemove.getKey(), current);
    }
  }

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
