package com.mrivanplays.webmessenger.dependency;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DependencyDownloader {

  // def repo url: https://repo1.maven.org/maven2/
  public static void resolveDependencies(
      List<Dependency> dependencies,
      String repositoryUrl,
      Path directory,
      Consumer<Throwable> loggingFunc,
      Consumer<Path> addToClasspath) {
    Map<URI, Path> resolvedUrls = new HashMap<>();
    for (var dependency : dependencies) {
      var jarName = dependency.artifactId() + "-" + dependency.version() + ".jar";
      var urlBuilder = new StringBuilder(repositoryUrl);
      Arrays.stream(dependency.groupId().split("\\."))
          .forEach(v -> urlBuilder.append(v).append('/'));
      urlBuilder
          .append(dependency.artifactId())
          .append('/')
          .append(dependency.version())
          .append('/')
          .append(jarName);
      resolvedUrls.put(URI.create(urlBuilder.toString()), directory.resolve(jarName));
    }
    for (var entry : resolvedUrls.entrySet()) {
      if (Files.exists(entry.getValue())) {
        addToClasspath.accept(entry.getValue());
        continue;
      }
      try {
        if (!Files.exists(directory)) {
          Files.createDirectory(directory);
        }
        var con = entry.getKey().toURL().openConnection();
        try (var is = con.getInputStream()) {
          Files.createFile(entry.getValue());
          Files.copy(is, entry.getValue(), StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        loggingFunc.accept(e);
      }
      addToClasspath.accept(entry.getValue());
    }
  }
}
