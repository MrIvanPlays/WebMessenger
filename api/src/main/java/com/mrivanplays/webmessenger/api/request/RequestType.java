package com.mrivanplays.webmessenger.api.request;

/**
 * Represents an enum of common HTTP request types to be used for registering {@link CustomRequest
 * custom requests}
 */
public enum RequestType {
  /**
   * Only requests of type GET will pass through a given route in a {@link RequestMetadata metadata}
   */
  GET,
  /**
   * Only requests of type POST will pass through a given route in a {@link RequestMetadata
   * metadata}
   */
  POST,
  /**
   * Only requests of type DELETE will pass through a given route in a {@link RequestMetadata
   * metadata}
   */
  DELETE,
  /** All requests to the given route in a {@link RequestMetadata metadta} will be passed through */
  ALL
}
