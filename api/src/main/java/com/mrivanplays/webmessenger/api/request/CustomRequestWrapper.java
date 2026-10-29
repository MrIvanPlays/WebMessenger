package com.mrivanplays.webmessenger.api.request;

import java.io.IOException;

final class CustomRequestWrapper implements CustomRequest {

  private CustomRequest wrapped;

  CustomRequestWrapper(CustomRequest wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public RequestMetadata metadata() {
    return wrapped.metadata();
  }

  @Override
  public void handle(RequestResponseContext ctx) throws IOException {
    Class<?> caller =
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
    try {
      if (!caller.isAssignableFrom(
          Class.forName("com.mrivanplays.webmessenger.core.web.CustomRequestRegistration"))) {
        throw new IllegalArgumentException(
            "CustomRequest#handle can only be invoked from WebMessenger plugin! SERVER ADMINS: This is (probably) not a bug with WebMessenger, if you see a plugin in this trace report to the plugin developer!");
      }
      wrapped.handle(ctx);
    } catch (ClassNotFoundException e) {
      // ignored cuz the class exists 100%
    }
  }
}
