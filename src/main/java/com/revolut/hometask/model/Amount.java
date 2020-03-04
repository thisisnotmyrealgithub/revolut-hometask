package com.revolut.hometask.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.hometask.utils.MoneyDeserializer;
import com.revolut.hometask.utils.MoneySerializer;

import java.math.BigDecimal;
import java.util.Objects;

public class Amount {

  @JsonSerialize(using = MoneySerializer.class)
  @JsonDeserialize(using = MoneyDeserializer.class)
  private BigDecimal amount;

  public Amount(BigDecimal amount) {
    this.amount = amount;
  }

  public Amount() {
  }

  // copy constructor
  public Amount(Amount balance) {
    this.amount =  balance.amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Amount balance = (Amount) o;
    return Objects.equals(amount, balance.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }
}
