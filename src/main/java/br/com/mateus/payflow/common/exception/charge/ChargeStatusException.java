package br.com.mateus.payflow.common.exception.charge;

public class ChargeStatusException extends RuntimeException {
    public ChargeStatusException(String message) {
        super(message);
    }
}
