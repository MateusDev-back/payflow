package br.com.mateus.payflow.common.exception;

public class CreditCardValidationException extends RuntimeException {
    public CreditCardValidationException(String message) {
        super(message);
    }
}
