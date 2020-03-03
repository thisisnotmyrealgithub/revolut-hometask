package com.revolut.hometask.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.hometask.model.Amount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MoneySerializerTest {

  @Test
  public void serializationTest() throws JsonProcessingException {
    var mapper = new ObjectMapper();

    var account = new Amount(new BigDecimal("40.2"));
    var json = mapper.writeValueAsString(account);
    assertEquals(json, "{\"amount\":\"40.20\"}");

    account = new Amount(new BigDecimal("40.2222222222222"));
    json = mapper.writeValueAsString(account);
    assertEquals(json, "{\"amount\":\"40.22\"}");

    account = new Amount(new BigDecimal("40"));
    json = mapper.writeValueAsString(account);
    assertEquals(json, "{\"amount\":\"40.00\"}");
  }

}
