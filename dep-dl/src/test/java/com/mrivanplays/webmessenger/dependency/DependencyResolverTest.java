package com.mrivanplays.webmessenger.dependency;

import com.google.gson.Gson;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DependencyResolverTest {

  static List<Dependency> knownValues =
      List.of(
          new Dependency("com.mrivanplays", "events", "1.0.0"),
          new Dependency("org.example", "test", "2.0.3"));

  @Test
  void testResolve() {
    var resolved =
        DependencyResolver.resolveDependencies(
            new Gson(), getClass().getClassLoader().getResourceAsStream("dependencies.json"));

    Assertions.assertEquals(knownValues, resolved);
  }
}
