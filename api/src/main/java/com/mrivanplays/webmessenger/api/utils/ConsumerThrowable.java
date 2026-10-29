package com.mrivanplays.webmessenger.api.utils;

public interface ConsumerThrowable<C, T extends Exception> {

  void accept(C c) throws T;

}
