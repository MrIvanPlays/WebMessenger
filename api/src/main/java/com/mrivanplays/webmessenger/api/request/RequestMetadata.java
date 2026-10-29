package com.mrivanplays.webmessenger.api.request;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.Arrays;
import java.util.List;

public interface RequestMetadata {

  static RequestMetadata simpleGet(Identifier identifier, String route) {
    return of(identifier, route, RequestType.GET);
  }

  static RequestMetadata simplePost(Identifier identifier, String route) {
    return of(identifier, route, RequestType.POST);
  }

  static RequestMetadata of(
      Identifier identifier, String route, RequestType type, CustomHeader... headers) {
    return of(identifier, route, type, Arrays.asList(headers));
  }

  static RequestMetadata of(
      Identifier identifier, String route, RequestType type, List<CustomHeader> headers) {
    return new RequestMetadata() {
      @Override
      public Identifier identifier() {
        return identifier;
      }

      @Override
      public String route() {
        return route;
      }

      @Override
      public RequestType type() {
        return type;
      }

      @Override
      public List<CustomHeader> requiredHeaders() {
        return headers;
      }
    };
  }

  Identifier identifier();

  String route();

  RequestType type();

  List<CustomHeader> requiredHeaders();
}
