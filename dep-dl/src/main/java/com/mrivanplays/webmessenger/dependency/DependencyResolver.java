package com.mrivanplays.webmessenger.dependency;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DependencyResolver {

  public static List<Dependency> resolveDependencies(Gson gsonInstance, InputStream res) {
    var array = gsonInstance.fromJson(new InputStreamReader(res), JsonArray.class);
    var ret = new ArrayList<Dependency>();
    for (var element : array) {
      var stringValue = element.getAsString();
      var parts = stringValue.split(":");
      ret.add(new Dependency(parts[0], parts[1], parts[2]));
    }
    return ret;
  }
}
