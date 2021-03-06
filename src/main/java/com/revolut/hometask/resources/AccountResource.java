package com.revolut.hometask.resources;

import com.revolut.hometask.exception.InsufficientFundsException;
import com.revolut.hometask.exception.WrongAmountException;
import com.revolut.hometask.model.Account;
import com.revolut.hometask.model.Amount;
import com.revolut.hometask.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
public class AccountResource {


    public AccountResource() {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Account createAccountWithMoney(Amount amount) throws WrongAmountException {
        return AccountService.getInstance().createAccount(amount);
    }

    @GET
    @Path("/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public Amount getBalance(@QueryParam("id") Long accountId) {
        return AccountService.getInstance().getAccountBalance(accountId);
    }

    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response moneyTransfer(Amount amount, @QueryParam("from") Long fromAccountId, @QueryParam("to") Long toAccountId) throws InsufficientFundsException, WrongAmountException {
        AccountService.getInstance().transferMoney(fromAccountId, toAccountId, amount);
        return Response
            .status(Response.Status.CREATED)
            .build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMoneyToAccount(Amount amount, @QueryParam("id") Long accountId) throws WrongAmountException {
        AccountService.getInstance().addMoneyToAccount(accountId, amount);
        return Response
            .status(Response.Status.OK)
            .build();
    }

    @POST
    @Path("/charge")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response charge(Amount amount, @QueryParam("id") Long accountId) throws InsufficientFundsException, WrongAmountException {
        AccountService.getInstance().chargeMoneyFromAccount(accountId, amount);
        return Response
            .status(Response.Status.OK)
            .build();
    }
}
