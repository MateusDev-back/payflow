package br.com.mateus.payflow.common.exception.charge;

public class ChargeNotFoundException extends RuntimeException {
    public ChargeNotFoundException() {
        super("Charge not found");
    }
}
