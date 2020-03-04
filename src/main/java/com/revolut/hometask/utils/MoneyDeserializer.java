package com.revolut.hometask.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class MoneyDeserializer extends JsonDeserializer<BigDecimal> {
  @Override
  public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    return p.getDecimalValue().setScale(2, BigDecimal.ROUND_DOWN);
  }
}

