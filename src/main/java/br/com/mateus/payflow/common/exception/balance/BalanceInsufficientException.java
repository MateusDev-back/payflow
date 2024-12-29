package br.com.mateus.payflow.common.exception.balance;

public class BalanceInsufficientException extends RuntimeException {
    public BalanceInsufficientException() {
        super("Insufficient balance");
    }
}
