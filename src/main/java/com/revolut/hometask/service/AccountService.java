package com.revolut.hometask.service;

import com.revolut.hometask.exception.InsufficientFundsException;
import com.revolut.hometask.exception.WrongAmountException;
import com.revolut.hometask.model.Account;
import com.revolut.hometask.model.Amount;

import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AccountService {

  private static final AccountService SINGLE_INSTANCE = new AccountService();
  AccountService() {}

  public static AccountService getInstance() {
    return SINGLE_INSTANCE;
  }

  private final AtomicLong ids = new AtomicLong();

  private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

  public Account createAccount() throws WrongAmountException {
    return createAccount(new Amount(new BigDecimal(0.0)));
  }

  public Account createAccount(Amount amount) throws WrongAmountException {
    if (amount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new WrongAmountException(amount.getAmount());
    }

    var id = ids.getAndIncrement();
    var account = new Account(id, amount);
    accounts.put(id, account);
    return new Account(account);
  }

  public Amount getAccountBalance(Long id) {
    var account = accounts.get(id);
    checkAccountExist(account, id);
    return new Amount(account.getAmount());
  }

  public void addMoneyToAccount(Long id, Amount addAmount) throws WrongAmountException {
    if (addAmount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new WrongAmountException(id, addAmount.getAmount());
    }

    var account = accounts.get(id);
    checkAccountExist(account, id);
    synchronized (account) {
      var amount = account.getAmount();
      var result = amount.getAmount().add(addAmount.getAmount());
      amount.setAmount(result);
    }
  }

  public void chargeMoneyFromAccount(Long id, Amount newBalance) throws InsufficientFundsException, WrongAmountException {
    chargeMoneyFromAccount(id, newBalance, false);
  }

  protected void chargeMoneyFromAccount(Long id, Amount chargeAmount, boolean withOverdraft) throws InsufficientFundsException, WrongAmountException {
    if (chargeAmount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new WrongAmountException(id, chargeAmount.getAmount());
    }

    var account = accounts.get(id);
    checkAccountExist(account, id);
    synchronized (account) {
      var amount = account.getAmount();
      if (withOverdraft || amount.getAmount().compareTo(chargeAmount.getAmount()) >= 0) {
        var result = amount.getAmount().subtract(chargeAmount.getAmount());
        amount.setAmount(result);
      } else {
        throw new InsufficientFundsException(
            account.getId(),
            amount.getAmount().subtract(chargeAmount.getAmount()).abs());
      }
    }
  }

  private void checkAccountExist(Account account, Long id) {
    if (account == null) {
      throw new NotFoundException("Account with id " + id + " doesn't exist");
    }
  }

  public void transferMoney(Long from, Long to, Amount amount) throws InsufficientFundsException, WrongAmountException {
    if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new WrongAmountException(from, amount.getAmount());
    }

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
        if (fromBalance.getAmount().compareTo(amount.getAmount()) >= 0) {
          fromBalance.setAmount(fromBalance.getAmount().subtract(amount.getAmount()));
          toBalance.setAmount(toBalance.getAmount().add(amount.getAmount()));
        } else {
          throw new InsufficientFundsException(
              accountFrom.getId(),
              fromBalance.getAmount().subtract(amount.getAmount()).abs());
        }
      }
    }
  }
}
