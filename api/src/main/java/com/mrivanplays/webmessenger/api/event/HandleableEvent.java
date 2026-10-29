package com.mrivanplays.webmessenger.api.event;

public interface HandleableEvent extends Event {

  Response response();

  void handle(Response response);

  interface Response {

    static Response handled() {
      return () -> true;
    }

    static Response unhandled() {
      return () -> false;
    }

    boolean isHandled();
  }

}
