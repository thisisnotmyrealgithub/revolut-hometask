package com.revolut.hometask.service;

import com.revolut.hometask.exception.InsufficientFundsException;
import com.revolut.hometask.model.Account;
import com.revolut.hometask.model.Amount;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.NotFoundException;

public class AccountServiceTest {

  private AccountService service;

  private int NUM_THREADS = 2;
  private int steps = 10_000;

  @BeforeEach
  public void setup() {
    this.service = new AccountService();
  }

  @Test
  public void createAccountSimpleTest() {
    var account = service.createAccount();

    assertEquals(account.getId(), 0);
    assertEquals(account.getAmount().getAmount(), new BigDecimal(0.0));

    var newBalance = new Amount(new BigDecimal(10.0));
    account = service.createAccount(newBalance);

    assertEquals(account.getId(), 1);
    assertEquals(account.getAmount(), newBalance);
  }

  @Test
  public void getAccountBalance() {
    var newBalance = new Amount(new BigDecimal(10.0));
    var account = service.createAccount(newBalance);
    var resultBalance = service.getAccountBalance(account.getId());
    assertEquals(newBalance, resultBalance);
  }

  @Test
  public void getWrongAccountBalance() {
    Assertions.assertThrows(NotFoundException.class, () -> {
      service.getAccountBalance(1);
    });
  }

  @Test
  public void getBalanceImmutableTest() {
    var account = service.createAccount();

    var balance = service.getAccountBalance(account.getId());
    assertEquals(balance.getAmount(), new BigDecimal(0.0));

    balance.setAmount(balance.getAmount().add(BigDecimal.valueOf(10.0)));

    balance = service.getAccountBalance(account.getId());
    assertEquals(balance.getAmount(), new BigDecimal(0.0));
  }

  @Test
  public void createAccountImmutableTest() {
    var account = service.createAccount();
    checkBalanceImmutable(account);

    account = service.createAccount(new Amount(BigDecimal.valueOf(1000.0)));
    checkBalanceImmutable(account);
  }

  private void checkBalanceImmutable(Account account) {
    var balance = account.getAmount();
    var amount = balance.getAmount();
    balance.setAmount(balance.getAmount().add(BigDecimal.valueOf(10.0)));

    balance = service.getAccountBalance(account.getId());
    assertEquals(balance.getAmount(), amount);
  }

  @Test
  public void addBalanceTesting() throws InterruptedException {
    var account = service.createAccount();
    var balance = new Amount(BigDecimal.valueOf(10.0));
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    List<Callable<Void>> tasks = new ArrayList<>();
    tasks.add(() -> {
      for (int i = 0; i < steps; i++) {
        service.addMoneyToAccount(account.getId(), balance);
      }
      return null;
    });
    tasks.add(() -> {
      for (int i = 0; i < steps; i++) {
        try {
          service.chargeMoneyFromAccount(account.getId(), balance, true);
        } catch (InsufficientFundsException ignore) {
        }
      }
      return null;
    });
    threadPool.invokeAll(tasks);
    var result = service.getAccountBalance(account.getId());
    result.getAmount();
    assertEquals(BigDecimal.valueOf(0.0), result.getAmount());
  }


  @Test
  public void chargeMoneyFailTest() {
    var accountAlice = service.createAccount(new Amount(BigDecimal.valueOf(10)));

    Assertions.assertThrows(InsufficientFundsException.class, () -> {
      service.chargeMoneyFromAccount(accountAlice.getId(), new Amount(BigDecimal.valueOf(100)));
    });
  }

  @Test
  public void transferMoneyFailTest() {
    var accountAlice = service.createAccount(new Amount(BigDecimal.valueOf(10)));
    var accountBob = service.createAccount(new Amount(BigDecimal.valueOf(100)));

    Assertions.assertThrows(InsufficientFundsException.class, () -> {
      service.transferMoney(accountAlice.getId(), accountBob.getId(), new Amount(BigDecimal.valueOf(100)));
    });
  }

  @Test
  public void transferMoneyTest() throws InterruptedException {
    double initSum = 1000.0;
    var accountAlice = service.createAccount(new Amount(BigDecimal.valueOf(initSum)));
    var accountBob = service.createAccount(new Amount(BigDecimal.valueOf(initSum)));

    var transferSum = new Amount(BigDecimal.valueOf(10.0));

    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    List<Callable<Void>> tasks = new ArrayList<>();
    tasks.add(() -> {
      for (int i = 0; i < steps; i++) {
        try {
          service.transferMoney(accountAlice.getId(), accountBob.getId(), transferSum);
        } catch (InsufficientFundsException ignore) {
        }
      }
      return null;
    });
    tasks.add(() -> {
      for (int i = 0; i < steps; i++) {
        try {
          service.transferMoney(accountBob.getId(), accountAlice.getId(), transferSum);
        } catch (InsufficientFundsException ignore) {
        }
      }
      return null;
    });
    threadPool.invokeAll(tasks);

    var aliceBalance = service.getAccountBalance(accountAlice.getId());
    var bobBalance = service.getAccountBalance(accountBob.getId());
    assertEquals(aliceBalance.getAmount().add(bobBalance.getAmount()), BigDecimal.valueOf(initSum + initSum));
  }

}
