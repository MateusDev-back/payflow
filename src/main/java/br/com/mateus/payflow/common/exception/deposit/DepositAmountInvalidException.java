package br.com.mateus.payflow.common.exception.deposit;

public class DepositAmountInvalidException extends RuntimeException {
    public DepositAmountInvalidException() {
        super("Deposit amount must be greater than zero");
    }
}
