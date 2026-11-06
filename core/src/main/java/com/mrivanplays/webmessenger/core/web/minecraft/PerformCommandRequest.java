package com.mrivanplays.webmessenger.core.web.minecraft;

import com.google.gson.JsonObject;
import com.mrivanplays.webmessenger.api.request.RequestMetadata;
import com.mrivanplays.webmessenger.core.WebMessengerPlugin;
import com.mrivanplays.webmessenger.core.web.MessengerHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class PerformCommandRequest extends MessengerHttpHandler {

  private final RequestMetadata metadata;

  public PerformCommandRequest(WebMessengerPlugin plugin) {
    super(plugin);
    this.metadata =
        RequestMetadata.simplePost(
            WebMessengerPlugin.API_IDENTIFIER,
            "/executeCommand",
            "Perform a command of your choice on the server. Expected JSON syntax on POST: {\"command\":\"<command to run>\"}");
  }

  @Override
  public RequestMetadata metadata() {
    return metadata;
  }

  // expected payload: "{"command":"<command to run>"}"
  @Override
  protected void handle0(HttpExchange exchange) throws IOException {
    JsonObject object = this.readJsonObject(exchange);
    if (object == null) {
      // readJsonObject handled error already, return
      return;
    }
    if (object.isEmpty() || !object.has("command")) {
      handleGenericErrorResponse(exchange, "Nothing provided to execute", 400);
      return;
    }
    String command = object.get("command").getAsString();
    getPlugin()
        .getDataProvider()
        .performCommand(command)
        .whenComplete((resp, e) -> {
          if (e != null) {
            getPlugin()
                .getLogger()
                .error("Exception when performing command", new RuntimeException(e));
            return;
          }
          JsonObject response = new JsonObject();
          try {
            if (resp) {
              response.addProperty("message", "Success");
              this.respond(exchange, response, 200);
            } else {
              response.addProperty("message", "Command execution failed");
              this.respond(exchange, response, 502);
            }
          } catch (IOException ex) {
            getPlugin().getLogger().error("Exception when responding", ex);
          }
        });
  }
}
