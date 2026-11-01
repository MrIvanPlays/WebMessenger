package com.mrivanplays.webmessenger.api.test;

import com.mrivanplays.webmessenger.api.event.Event;
import com.mrivanplays.webmessenger.api.event.EventBus;
import com.mrivanplays.webmessenger.api.event.EventListener;
import com.mrivanplays.webmessenger.api.event.EventPriority;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EventBusTest {

  static class TestEvent implements Event {

    final AtomicInteger called = new AtomicInteger(0);
  }

  static class PrioritizedTest implements Event {

    List<EventPriority> order = new ArrayList<>();
  }

  static EventListener<TestEvent> listener =
      EventListener.of(event -> event.called.incrementAndGet());
  static BiConsumer<EventPriority, PrioritizedTest> prioritizedListener =
      (priority, event) -> {
        System.out.println("Run order priority: " + priority.name());
        event.order.add(priority);
      };
  static ExecutorService executor = Executors.newFixedThreadPool(10);

  @BeforeAll
  static void init() {
    EventBus.INSTANCE.registerListener(CommonValues.TEST_IDENTIFIER, TestEvent.class, listener);
    EventBus.INSTANCE.registerListener(
        CommonValues.TEST_IDENTIFIER,
        PrioritizedTest.class,
        EventPriority.NORMAL,
        event -> prioritizedListener.accept(EventPriority.NORMAL, event));
    EventBus.INSTANCE.registerListener(
        CommonValues.TEST_IDENTIFIER,
        PrioritizedTest.class,
        EventPriority.LAST,
        event -> prioritizedListener.accept(EventPriority.LAST, event));
    EventBus.INSTANCE.registerListener(
        CommonValues.TEST_IDENTIFIER,
        PrioritizedTest.class,
        EventPriority.FIRST,
        event -> prioritizedListener.accept(EventPriority.FIRST, event));
  }

  @Test
  void testFire() {
    TestEvent event = EventBus.INSTANCE.fire(new TestEvent());

    Assertions.assertEquals(1, event.called.get());
  }

  @Test
  void testUnregister() {
    EventBus.INSTANCE.unregisterListener(CommonValues.TEST_IDENTIFIER, TestEvent.class);
    TestEvent event = EventBus.INSTANCE.fire(new TestEvent());

    Assertions.assertEquals(0, event.called.get());
  }

  @Test
  void testPriorityCall() {
    PrioritizedTest event = EventBus.INSTANCE.fire(new PrioritizedTest());

    Assertions.assertEquals(3, event.order.size());
    Assertions.assertSame(EventPriority.FIRST, event.order.getFirst());
    Assertions.assertSame(EventPriority.NORMAL, event.order.get(1));
    Assertions.assertSame(EventPriority.LAST, event.order.get(2));
  }

  @Test
  void concurrencyTest() {
    TestEvent event = new TestEvent();
    IntStream.range(0, 1001)
        .forEach(_ ->
            executor.submit(() -> {
              EventBus.INSTANCE.fire(event);
            }));
  }
}
