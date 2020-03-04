package com.revolut.hometask.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;

@Provider
public class WrongAmountException extends Throwable implements
    ExceptionMapper<WrongAmountException> {

  private Long account;
  private BigDecimal sum;

  public WrongAmountException() {
  }

  public WrongAmountException(Long account, BigDecimal sum) {
    this.account = account;
    this.sum = sum;
  }

  public WrongAmountException(BigDecimal amount) {
    this.sum = amount;
  }

  @Override
  public String getMessage() {
    var sumString = sum.setScale(2).toPlainString();
    if (account != null) {
      return "Wrong amount " + sumString + " in operation with " + account + " account";
    } else {
      return "Wrong amount " + sumString + " in operation with creating account";
    }
  }

  @Override
  public Response toResponse(WrongAmountException exception) {
    return Response.status(400).entity(exception.getMessage()).type("text/plain").build();
  }
}
