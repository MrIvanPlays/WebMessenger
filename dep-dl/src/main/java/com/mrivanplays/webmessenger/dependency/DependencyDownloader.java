/*
 * Copyright (c) Ivan Pekov 2026
 * Copyright (c) Contributors 2026
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

  public static void resolveDependencies(
      List<Dependency> dependencies,
      String repositoryUrl,
      Path directory,
      Consumer<Object> loggingFunc,
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
      loggingFunc.accept("Downloading dependency " + entry.getKey().toString());
      try {
        if (!Files.exists(directory)) {
          Files.createDirectory(directory);
        }
        var con = entry.getKey().toURL().openConnection();
        try (var is = con.getInputStream()) {
          Files.createFile(entry.getValue());
          Files.copy(is, entry.getValue(), StandardCopyOption.REPLACE_EXISTING);
        }
        addToClasspath.accept(entry.getValue());
        loggingFunc.accept("Dependency '" + entry.getValue().getFileName() + "' downloaded");
      } catch (IOException e) {
        loggingFunc.accept(e);
      }
    }
  }
}
