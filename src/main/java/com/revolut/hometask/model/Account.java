package com.revolut.hometask.model;

import java.util.Objects;

public class Account {

  private Integer id;

  private Amount amount;

  public Account(Integer id, Amount balance) {
    this.id = id;
    this.amount = balance;
  }

  public Account() {
  }

  // copy constructor
  public Account(Account that) {
    this.id = that.id;
    this.amount = new Amount(that.amount);
  }

  public Integer getId() {
    return id;
  }

  public Amount getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return Objects.equals(id, account.id) &&
        Objects.equals(amount, account.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, amount);
  }
}
