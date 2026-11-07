package com.mrivanplays.webmessenger.velocity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class RepositoryLinkResolver {

  public static final Gson GSON = new Gson();

  public static String resolve(Path dataDirectory, ClassLoader loader) {
    var file = dataDirectory.resolve("repository.json");
    if (!Files.exists(file)) {
      try (var is = loader.getResourceAsStream("repository.json")) {
        Files.copy(is, file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return "https://repo1.maven.org/maven2/"; // do not parse
    }
    try (var reader = Files.newInputStream(file)) {
      var obj = GSON.fromJson(new InputStreamReader(reader), JsonObject.class);
      return obj.get("repository").getAsString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
