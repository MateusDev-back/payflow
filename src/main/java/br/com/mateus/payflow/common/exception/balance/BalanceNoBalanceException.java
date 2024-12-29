package br.com.mateus.payflow.common.exception.balance;

public class BalanceNoBalanceException extends RuntimeException {
    public BalanceNoBalanceException() {
        super("Insufficient balance");
    }
}
