package br.com.mateus.payflow.enums.payment;

import java.util.Arrays;

public enum PaymentMethod {
    BALANCE,
    CREDIT_CARD;

    public static PaymentMethod fromString(String method) {
        return Arrays.stream(values())
                .filter(m -> m.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pagamento inválido!"));
    }
}
