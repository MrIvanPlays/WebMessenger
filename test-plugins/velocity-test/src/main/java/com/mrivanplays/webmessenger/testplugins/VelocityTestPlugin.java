package com.mrivanplays.webmessenger.testplugins;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

public class VelocityTestPlugin {

  @Inject private ProxyServer proxy;
  @Inject private Logger logger;

  @Subscribe
  public void onInit(ProxyInitializeEvent event) {
    // again isolated because during the construction phase libraries
    // are yet to be present and velocity WILL throw NoClassDefError
    com.mrivanplays.webmessenger.testplugins.IsolatedBoot.boot(logger);
  }
}
