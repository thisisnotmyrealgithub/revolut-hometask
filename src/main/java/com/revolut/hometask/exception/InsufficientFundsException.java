package com.revolut.hometask.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;

@Provider
public class InsufficientFundsException extends Throwable implements
    ExceptionMapper<InsufficientFundsException> {

  private Integer account;
  private BigDecimal sum;

  public InsufficientFundsException() {
  }

  public InsufficientFundsException(Integer account, BigDecimal sum) {
    this.account = account;
    this.sum = sum;
  }

  @Override
  public String getMessage() {
    var sumString = sum.setScale(2).toPlainString();
    return "There is not enough " + sumString + " on " + account + " account";
  }

  @Override
  public Response toResponse(InsufficientFundsException exception) {
    return Response.status(400).entity(exception.getMessage()).type("text/plain").build();
  }
}
