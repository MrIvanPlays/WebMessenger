package com.mrivanplays.webmessenger.core.apiimpl;

import com.mrivanplays.webmessenger.api.request.header.CustomHeader;
import java.util.List;

public record HttpExchangeCustomHeader(String key, List<String> value) implements CustomHeader {}
