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
    assertEquals(json, "{\"amount\":40.20}");

    account = new Amount(new BigDecimal("40.2222222222222"));
    json = mapper.writeValueAsString(account);
    assertEquals(json, "{\"amount\":40.22}");

    account = new Amount(new BigDecimal("40"));
    json = mapper.writeValueAsString(account);
    assertEquals(json, "{\"amount\":40.00}");
  }

  @Test
  public void deserializationTest() throws JsonProcessingException {
    var mapper = new ObjectMapper();

    var amount = mapper.readValue("{\"amount\": 40.20 }", Amount.class);
    assertEquals(amount.getAmount(), new BigDecimal("40.20"));

    amount = mapper.readValue("{\"amount\": 0.01 }", Amount.class);
    assertEquals(amount.getAmount(), new BigDecimal("0.01"));

    amount = mapper.readValue("{\"amount\": 0.001}", Amount.class);
    assertEquals(amount.getAmount(), new BigDecimal("0.00"));

    amount = mapper.readValue("{\"amount\": 0.00}", Amount.class);
    assertEquals(amount.getAmount(), new BigDecimal("0.00"));

    amount = mapper.readValue("{\"amount\": 0.999}", Amount.class);
    assertEquals(amount.getAmount(), new BigDecimal("0.99"));
  }

}
