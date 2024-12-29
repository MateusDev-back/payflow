package br.com.mateus.payflow.common.exception.user;

public class PayerNotFoundException extends RuntimeException {
    public PayerNotFoundException() {
        super("Payer not found");
    }
}
