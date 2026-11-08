package com.mrivanplays.webmessenger.testplugins;

import com.mrivanplays.webmessenger.api.Identifier;
import com.mrivanplays.webmessenger.api.WebMessengerServer;
import com.mrivanplays.webmessenger.api.event.HttpRequestHandleEvent;
import org.slf4j.Logger;

public class IsolatedBoot {

  static void boot(Logger logger) {
    WebMessengerServer.getInstance()
        .eventBus()
        .registerListener(
            new Identifier("webmessenger", "test"),
            HttpRequestHandleEvent.class,
            e ->
                logger.info(
                    "WebMessenger is handling request '" + e.context().metadata().route() + "'"));
  }
}
