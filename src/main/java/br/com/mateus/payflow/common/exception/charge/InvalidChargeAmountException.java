package br.com.mateus.payflow.common.exception.charge;

public class InvalidChargeAmountException extends RuntimeException {
    public InvalidChargeAmountException() {
        super("Charge amount must be greater than zero");
    }
}
