package com.mrivanplays.webmessenger.api.utils;

/**
 * Represents a {@link java.util.function.Consumer} which when accepts the value can throw an {@link
 * Exception}
 *
 * @param <C> consumed value
 * @param <T> thrown exception
 */
public interface ConsumerThrowable<C, T extends Exception> {

  void accept(C c) throws T;
}
