package com.revolut.hometask.service;

import com.revolut.hometask.exception.InsufficientFundsException;
import com.revolut.hometask.model.Account;
import com.revolut.hometask.model.Amount;

import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountService {

  private static final AccountService SINGLE_INSTANCE = new AccountService();
  AccountService() {}
  public static AccountService getInstance() {
    return SINGLE_INSTANCE;
  }

  private final AtomicInteger ids = new AtomicInteger();

  private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

  public Account createAccount() {
    return createAccount(new Amount(new BigDecimal(0.0)));
  }

  public Account createAccount(Amount balance) {
    var id = ids.getAndIncrement();
    var account = new Account(id, balance);
    accounts.put(id, account);
    return new Account(account);
  }

  public Amount getAccountBalance(Integer id) {
    var account = accounts.get(id);
    checkAccountExist(account, id);
    return new Amount(account.getAmount());
  }

  public void addMoneyToAccount(Integer id, Amount newBalance) {
    var account = accounts.get(id);
    checkAccountExist(account, id);
    synchronized (account) {
      var balance = account.getAmount();
      var result = balance.getAmount().add(newBalance.getAmount());
      balance.setAmount(result);
    }
  }

  public void chargeMoneyFromAccount(Integer id, Amount newBalance) throws InsufficientFundsException {
    chargeMoneyFromAccount(id, newBalance, false);
  }

  protected void chargeMoneyFromAccount(Integer id, Amount amount, boolean withOverdraft) throws InsufficientFundsException {
    var account = accounts.get(id);
    checkAccountExist(account, id);
    synchronized (account) {
      var balance = account.getAmount();
      if (withOverdraft || balance.getAmount().compareTo(amount.getAmount()) >= 0) {
        var result = balance.getAmount().subtract(amount.getAmount());
        balance.setAmount(result);
      } else {
        throw new InsufficientFundsException(
            account.getId(),
            balance.getAmount().subtract(amount.getAmount()).abs());
      }
    }
  }

  private void checkAccountExist(Account account, Integer id) {
    if (account == null) {
      throw new NotFoundException("Account with id " + id + " doesn't exist");
    }
  }

  public void transferMoney(Integer from, Integer to, Amount balance) throws InsufficientFundsException {
    if (from.equals(to)) {
      return;
    }

    var accountFrom = accounts.get(from);
    checkAccountExist(accountFrom, from);

    var accountTo = accounts.get(to);
    checkAccountExist(accountTo, to);

    Account lock1;
    Account lock2;

    if (accountFrom.getId().compareTo(accountTo.getId()) < 0) {
      lock1 = accountFrom;
      lock2 = accountTo;
    } else {
      lock1 = accountTo;
      lock2 = accountFrom;
    }

    synchronized (lock1) {
      synchronized (lock2) {
        var fromBalance = accountFrom.getAmount();
        var toBalance = accountTo.getAmount();
        if (fromBalance.getAmount().compareTo(balance.getAmount()) >= 0) {
          fromBalance.setAmount(fromBalance.getAmount().subtract(balance.getAmount()));
          toBalance.setAmount(toBalance.getAmount().add(balance.getAmount()));
        } else {
          throw new InsufficientFundsException(
              accountFrom.getId(),
              fromBalance.getAmount().subtract(balance.getAmount()).abs());
        }
      }
    }
  }
}
