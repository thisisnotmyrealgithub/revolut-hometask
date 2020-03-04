package com.revolut.hometask.utils;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class MoneySerializer extends JsonSerializer<BigDecimal> {
  @Override
  public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeNumber(value.setScale(2, BigDecimal.ROUND_DOWN).toString());
  }
}
